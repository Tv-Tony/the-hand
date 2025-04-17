package tv.toner.riggedHand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;

import com.javafx.experiments.importers.SmoothingGroups;
import com.javafx.experiments.importers.maya.Joint;
import com.javafx.experiments.shape3d.PolygonMesh;
import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SkinningMesh;

import javafx.geometry.Point3D;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Translate;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.toner.utils.Axes;

/**
 * @author Alexander Kouznetsov
 * Modified by José Pereda
 * Modified by Antonin Vychodil
 */
@Getter
@Setter
public class HandImporter {

    private static final Logger log = LogManager.getLogger(HandImporter.class);
    private static final String ANSI_BLUE  = "\u001B[34m";
    private static final String ANSI_RESET = "\u001B[0m";

    private final String nameFile;
    private final JsonReader reader;
    private final List<Parent> jointForest = new ArrayList<>();
    private PolygonMeshView skinningMeshView;
    private final boolean skeletal;
    private final boolean axes;

    public HandImporter(String nameFile, boolean skeletal, boolean axes) {
        this.nameFile = nameFile;
        this.skeletal  = skeletal;
        this.axes      = axes;
        this.reader    = JsonProvider.provider()
                .createReader(HandImporter.class.getResourceAsStream("/" + nameFile));
    }

    public void readModel() {
        readModel(1f);
    }

    public void readModel(float scale) {
        if (reader == null) {
            log.warn("No model reader for '{}'", nameFile);
            return;
        }

        JsonObject object      = reader.readObject();
        JsonArray  verticesArr  = object.getJsonArray("vertices");
        JsonArray  uvsArr       = object.getJsonArray("uvs").isEmpty() ? null
                : object.getJsonArray("uvs").getJsonArray(0);
        JsonArray  facesArr     = object.getJsonArray("faces");
        JsonArray  normalsArr   = object.getJsonArray("normals");

        // capture counts for summary
        int vertexCount = verticesArr.size();
        int uvCount     = (uvsArr == null ? 0 : uvsArr.size());
        int faceCount   = facesArr.size();

        JsonObject metadata     = object.getJsonObject("metadata");
        int    facesNumber      = metadata.getInt("faces");
        int    nPoints          = metadata.getInt("vertices");
        int    texCoordsNumber  = metadata.getJsonArray("uvs").isEmpty()
                ? 1
                : metadata.getJsonArray("uvs").getInt(0);
        int    nJoints          = metadata.getInt("bones");

        // build the mesh exactly as before...
        final int MINMAXLEN = vertexCount / nPoints; // typically 3
        float[] min = new float[MINMAXLEN];
        float[] max = new float[MINMAXLEN];
        Arrays.fill(min, Float.MAX_VALUE);
        Arrays.fill(max, -Float.MAX_VALUE);

        PolygonMesh polygonMesh = new PolygonMesh();
        polygonMesh.getPoints().ensureCapacity(nPoints);
        polygonMesh.getTexCoords().ensureCapacity(texCoordsNumber);
        polygonMesh.getFaceSmoothingGroups().ensureCapacity(facesNumber);

        // points
        for (int i = 0; i < vertexCount; i++) {
            float c = (float) (scale * verticesArr.getJsonNumber(i).doubleValue());
            polygonMesh.getPoints().addAll(c);
            int d = i % MINMAXLEN;
            min[d] = Math.min(min[d], c);
            max[d] = Math.max(max[d], c);
        }

        // uvs
        if (uvsArr != null) {
            for (int i = 0; i < uvCount; i++) {
                polygonMesh.getTexCoords().addAll((float) uvsArr.getJsonNumber(i).doubleValue());
            }
        } else {
            // create a dummy UV so we don’t break indexing
            for (int i = 0; i < texCoordsNumber; i++) {
                polygonMesh.getTexCoords().addAll(0f);
            }
        }

        // normals → smoothing groups
        float[] rawNormals = new float[normalsArr.size()];
        for (int i = 0; i < rawNormals.length; i++) {
            rawNormals[i] = (float) normalsArr.getJsonNumber(i).doubleValue();
        }

        // unpack faces
        final int LEN = facesArr.size() / facesNumber;
        final int V1  = 1, V2 = 2, V3 = 3;
        final int UV1 = 5, UV2 = 6, UV3 = 7;
        final int N1  = (uvsArr != null ? 8 : 5),
                N2  = (uvsArr != null ? 9 : 6),
                N3  = (uvsArr != null ? 10 : 7);

        int[][] pfaces   = new int[facesNumber][];
        int[][] pnormals = new int[facesNumber][];
        for (int i = 0; i < facesArr.size(); i += LEN) {
            pfaces[i / LEN]   = new int[] {
                    facesArr.getInt(i + V1),  (uvsArr != null ? facesArr.getInt(i + UV1) : 0),
                    facesArr.getInt(i + V2),  (uvsArr != null ? facesArr.getInt(i + UV2) : 0),
                    facesArr.getInt(i + V3),  (uvsArr != null ? facesArr.getInt(i + UV3) : 0)
            };
            pnormals[i / LEN] = new int[] {
                    facesArr.getInt(i + N1),
                    facesArr.getInt(i + N2),
                    facesArr.getInt(i + N3)
            };
        }
        polygonMesh.faces = pfaces;
        int[] smooth = SmoothingGroups.calcSmoothGroups(pfaces, pnormals, rawNormals);
        polygonMesh.getFaceSmoothingGroups().setAll(smooth);

        // joints & skinning
        float[][]   weights        = new float[nJoints][nPoints];
        Affine[]    bindTransforms = new Affine[nJoints];
        Affine      bindGlobal     = new Affine();
        List<Joint> joints         = new ArrayList<>(nJoints);

        for (int i = 0; i < nJoints; i++) {
            JsonObject bone = object.getJsonArray("bones").getJsonObject(i);
            Joint      j    = new Joint();
            String     id   = bone.getString("name");
            j.setId(id);

            // position
            JsonArray pos = bone.getJsonArray("pos");
            double x = scale * pos.getJsonNumber(0).doubleValue();
            double y = scale * pos.getJsonNumber(1).doubleValue();
            double z = scale * pos.getJsonNumber(2).doubleValue();
            j.t.setX(x);
            j.t.setY(y);
            j.t.setZ(z);

            int parentIdx = bone.getInt("parent");
            if (parentIdx == -1) {
                if (axes) j.getChildren().add(new Axes(1));
                jointForest.add(j);
                bindTransforms[i] = new Affine(new Translate(-x, -y, -z));
            } else {
                if (axes) j.getChildren().add(new Axes(1));
                Joint parent = joints.get(parentIdx);
                parent.getChildren().add(j);
                if (skeletal) {
                    parent.getChildren().add(new Bone(1, new Point3D(x, y, z)));
                }
                try {
                    bindTransforms[i] = new Affine(j.getLocalToSceneTransform().createInverse());
                } catch (NonInvertibleTransformException ex) {
                    log.error("Failed to invert transform for joint '{}': {}", id, ex.getMessage());
                }
            }
            joints.add(j);
        }

        // skin weights
        JsonArray skinIdxs = object.getJsonArray("skinIndices");
        JsonArray skinWts  = object.getJsonArray("skinWeights");
        for (int i = 0; i < skinIdxs.size(); i += 2) {
            int   pIndex = i / 2;
            int   j1     = skinIdxs.getInt(i);
            int   j2     = skinIdxs.getInt(i + 1);
            float w1     = (float) skinWts.getJsonNumber(i).doubleValue();
            float w2     = (float) skinWts.getJsonNumber(i + 1).doubleValue();
            float sum    = w1 + w2;
            w1 /= sum; w2 /= sum;
            if (weights[j1][pIndex] == 0) weights[j1][pIndex] = w1;
            if (weights[j2][pIndex] == 0) weights[j2][pIndex] = w2;
        }

        SkinningMesh    sm    = new SkinningMesh(polygonMesh, weights, bindTransforms, bindGlobal, joints, jointForest);
        skinningMeshView      = new PolygonMeshView(sm);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.SANDYBROWN);
        skinningMeshView.setMaterial(material);
        if (skeletal) {
            skinningMeshView.setDrawMode(DrawMode.LINE);
        }
        skinningMeshView.setCullFace(CullFace.BACK);

        // ─── concise, colored summary ────────────────────────────────────────────────────
        String handSide = nameFile.toLowerCase().contains("left")
                ? "Left"
                : nameFile.toLowerCase().contains("right")
                ? "Right"
                : nameFile;
        List<String> boneNames = joints.stream()
                .map(Joint::getId)
                .collect(Collectors.toList());

        String summary = new StringBuilder()
                .append(ANSI_BLUE)
                .append("🖐️  Loaded ").append(handSide).append(" Hand")
                .append(ANSI_RESET).append("\n")
                .append(ANSI_BLUE)
                .append(" → Vertices: ").append(vertexCount)
                .append(ANSI_RESET).append("\n")
                .append(ANSI_BLUE)
                .append(" → UVs:      ").append(uvCount)
                .append(ANSI_RESET).append("\n")
                .append(ANSI_BLUE)
                .append(" → Faces:    ").append(faceCount)
                .append(ANSI_RESET).append("\n")
                .append(ANSI_BLUE)
                .append(" → Bones:    ").append(boneNames.size())
                .append(" (").append(String.join(", ", boneNames)).append(")")
                .append(ANSI_RESET)
                .toString();

        log.info(summary);
    }
}
