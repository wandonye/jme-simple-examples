/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpleEditor;

import com.entitysystem.ComponentsControl;
import com.entitysystem.EntityModelPathComponent;
import com.entitysystem.EntityNameComponent;
import com.entitysystem.EntitySpatialsControl_2;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mifth
 */
public class EditorSceneManager {

    private AssetManager assetMan;
    private Node root, guiNode;
    private Application app;
    private EditorBaseManager base;
    private final JFileChooser mFileCm;
    private FileFilter modFilter = new EditorSceneFilter();
    private String scenePath = null;
    private static List<String> assetsList = new ArrayList<String>();
//    private static List<String> entitiesListsList = new ArrayList<String>();
    private static ConcurrentHashMap<String, String> entitiesList = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, Spatial> spatialsList = new ConcurrentHashMap<String, Spatial>();
//    private EntityManager entityManager;
    private DesktopAssetManager dsk;

    public EditorSceneManager(Application app, EditorBaseManager base) {

        this.app = app;
        this.base = base;
        assetMan = this.app.getAssetManager();
        root = (Node) this.app.getViewPort().getScenes().get(0);
        guiNode = (Node) this.app.getGuiViewPort().getScenes().get(0);

        mFileCm = new JFileChooser();
        mFileCm.addChoosableFileFilter(modFilter);
//        mFileCm.addChoosableFileFilter(texFilter);
        mFileCm.setAcceptAllFileFilterUsed(false);
        mFileCm.setPreferredSize(new Dimension(800, 600));

        dsk = (DesktopAssetManager) assetMan;
//        entityManager = base.getEntityManager();
    }

    protected void newScene() {
    }

    protected void loadScene() {
        mFileCm.setDialogType(JFileChooser.OPEN_DIALOG);
        mFileCm.setDialogTitle("Load Scene");
        mFileCm.setApproveButtonToolTipText("Open");
        mFileCm.setApproveButtonText("Open");
        mFileCm.setFileFilter(modFilter);
        int returnVal = mFileCm.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File file = mFileCm.getSelectedFile();
            System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRR");
        }
    }

    protected void saveScene() {
    }

    protected void saveAsNewScene() {
        mFileCm.setDialogType(JFileChooser.SAVE_DIALOG);
        mFileCm.setDialogTitle("Save Scene");
        mFileCm.setApproveButtonToolTipText("Save");
        String s = "Save";
        mFileCm.setApproveButtonText("Save");
        mFileCm.setFileFilter(modFilter);
        int returnVal = mFileCm.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = mFileCm.getSelectedFile();

            if (selectedFile.getName().indexOf(".") != 0 && selectedFile.getName().length() > 0) {
                String filePath = selectedFile.getParent();
                filePath = correctPath(filePath);

                String fileName = selectedFile.getName();
                if (fileName.indexOf(".") > 0) {
                    fileName = fileName.substring(0, fileName.indexOf("."));
                }
                fileName = fileName + ".swe";
                String fullPath = filePath + fileName;

                JSONObject saveSceneJson = new JSONObject();
//                JSONArray listLayers = new JSONArray();

                //save layers
                for (Node layerNode : base.getLayerManager().getLayers()) {
                    JSONObject layerToSave = new JSONObject();

                    // save ID entities
                    for (Spatial sp : layerNode.getChildren()) {
                        JSONObject entityToSave = new JSONObject();

                        Object idObj = sp.getUserData("EntityID");
                        long idLong = (Long) idObj;

                        // save name
                        EntityNameComponent nameComp = (EntityNameComponent) base.getEntityManager().getComponent(idLong, EntityNameComponent.class);
                        entityToSave.put("IDName", nameComp.getName());

//                        EntityModelPathComponent pathComp = (EntityModelPathComponent) base.getEntityManager().getComponent(idLong, EntityModelPathComponent.class);
                        entityToSave.put("IDModel", nameComp.getName().substring(0, nameComp.getName().indexOf("_IDX")) + ".j3o");

                        // save transforms
                        Transform trID = sp.getWorldTransform();
                        JSONObject transformToSave = new JSONObject();
                        transformToSave.put("translationX", trID.getTranslation().getX());
                        transformToSave.put("translationY", trID.getTranslation().getY());
                        transformToSave.put("translationZ", trID.getTranslation().getZ());
                        transformToSave.put("rotationX", trID.getRotation().getX());
                        transformToSave.put("rotationY", trID.getRotation().getY());
                        transformToSave.put("rotationZ", trID.getRotation().getZ());
                        transformToSave.put("rotationW", trID.getRotation().getW());
                        transformToSave.put("scaleX", trID.getScale().getX());
                        transformToSave.put("scaleY", trID.getScale().getY());
                        transformToSave.put("scaleZ", trID.getScale().getZ());
                        entityToSave.put("IDTransform", transformToSave);

                        // seve data components of entity
                        ConcurrentHashMap<String, String> entityData = base.getDataManager().getEntityData(idLong);
                        for (String strKey : entityData.keySet()) {
                            JSONObject dataComponentsToSave = new JSONObject();
                            dataComponentsToSave.put(strKey, entityData.get(strKey));
                            entityToSave.put("IDDataComponents", dataComponentsToSave);
                        }

                        layerToSave.put(idLong, entityToSave);
                    }
//                    layer.
                    saveSceneJson.put(layerNode.getName(), layerToSave);
//                    saveSceneJson.
//                    listLayers.add(layerNode.getName());
                }
//                saveSceneJson.put("messages", listLayers);

                try {
                    File saveFile = new File(fullPath);
                    saveFile.setReadable(true);
                    saveFile.setWritable(true);

                    FileWriter fileToSave = new FileWriter(saveFile);
                    fileToSave.write(saveSceneJson.toJSONString());
                    fileToSave.flush();
                    fileToSave.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRR" + fullPath);
            }
        }
    }

    protected void clearScene() {
        // clear selection
        base.getSelectionManager().clearSelectionList();
        base.getSelectionManager().calculateSelectionCenter();

        // remove all spatials from layers
        for (Node layer : base.getLayerManager().getLayers()) {
            layer.detachAllChildren();
            layer.getParent().detachChild(layer);
        }

        // clear history
        ArrayList<EditorHistoryObject> hList = base.getHistoryManager().getHistoryList();
        for (EditorHistoryObject hObj : hList) {
            hObj.clearHistoryObject();
            hObj = null;
        }
        hList.clear();

        // clear data components
        base.getDataManager().clearEntityData();
        
        //clear gui
        base.getGuiManager().clearGui();

        // clear entities
        ConcurrentHashMap<Long, ComponentsControl> allControls = base.getEntityManager().getAllControls();
        for (Long ID : allControls.keySet()) {
            base.getEntityManager().removeEntity(ID);
            base.getSpatialSystem().removeSpatialControl(ID);
        }
        allControls.clear();
        dsk.clearCache(); // clear all loaded models
        
        // clear assets list
        for (String str : assetsList) {
            assetMan.unregisterLocator(str, FileLocator.class);
        }
        assetsList.clear();
    }

    protected void addAsset(String path) {
        String thePath = correctPath(path);
        File fl = new File(path);

        // registerLoacetor
        if (fl.exists() && assetsList.contains(thePath) == false) {
            
            // unsupported now
//            if (thePath.endsWith(".jar") || thePath.endsWith(".zip")) {
//                assetMan.registerLocator(thePath, ZipLocator.class);
//            } 
            
                assetMan.registerLocator(thePath, FileLocator.class);
            

            assetsList.add(thePath);
            findFiles(thePath, "j3o");
        }
    }

    private Long createEntityModel(String name, String path) {
        Node activeLayer = base.getLayerManager().getActiveLayer();

        if (activeLayer != null) {
            // setup Entity
            Node model = null;
            if (spatialsList.get(path) == null) {
                model = (Node) dsk.loadModel(path);
                spatialsList.put(path, model);
                model = model.clone(false);
            } else {
                model = (Node) spatialsList.get(path).clone(false);
            }

            Vector3f camHelperPosition = base.getCamManager().getCamTrackHelper().getWorldTranslation();
            model.setLocalTranslation(camHelperPosition);


            long ent = base.getEntityManager().createEntity();
            base.getDataManager().setEntityData(ent, new ConcurrentHashMap<String, String>());
            ComponentsControl components = base.getEntityManager().getComponentControl(ent);

            EntityModelPathComponent modelPath = new EntityModelPathComponent(path);
            components.setComponent(modelPath);

            EntityNameComponent nameComponent = new EntityNameComponent(name + "_IDX" + ent);
            components.setComponent(nameComponent);
            model.setName(nameComponent.getName());



            EntitySpatialsControl_2 spatialControl = base.getSpatialSystem().addSpatialControl(model, ent, base.getEntityManager().getComponentControl(ent));
            spatialControl.setType(EntitySpatialsControl_2.SpatialType.Node);
            spatialControl.recurseNodeID(model);

            activeLayer.attachChild(model);

            return ent;
        }
        return null;
    }

    protected void removeClones(String name) {
        String nameToRemove = name + "_IDX";
        List<Long> selList = base.getSelectionManager().getSelectionList();
        List<Long> idsToRemove = new ArrayList<Long>();
        for (Long id : selList) {

            // remove objects from the scene
            EntityNameComponent nameComp = (EntityNameComponent) base.getEntityManager().getComponent(id, EntityNameComponent.class);
            if (nameComp.getName().indexOf(nameToRemove) == 0) {
                idsToRemove.add(id);
            }
        }

        for (Long removeID : idsToRemove) {
            EntityNameComponent nameToRemoveReal = (EntityNameComponent) base.getEntityManager().getComponent(removeID, EntityNameComponent.class);
            base.getGuiManager().getSceneObjectsListBox().removeItem(nameToRemoveReal.getName());
            System.out.println("yeeee" + nameToRemoveReal.getName());
            removeEntityObject(removeID);
        }
        idsToRemove.clear();
        idsToRemove = null;
        base.getSelectionManager().calculateSelectionCenter();
    }

    protected List<Long> cloneSelectedEntities() {
        List<Long> selectionList = base.getSelectionManager().getSelectionList();
        List<Long> tempList = new ArrayList<Long>();
        for (Long idOfSelected : selectionList) {
            // selected entity's components
            ComponentsControl compControlSelected = base.getEntityManager().getComponentControl(idOfSelected);
            EntityModelPathComponent modelPathSelected = (EntityModelPathComponent) compControlSelected.getComponent(EntityModelPathComponent.class);
            Node selectedModel = (Node) base.getSpatialSystem().getSpatialControl(idOfSelected).getGeneralNode();
            Node layerToClone = selectedModel.getParent();
            EntityNameComponent modelNameSelected = (EntityNameComponent) compControlSelected.getComponent(EntityNameComponent.class);

            // new entity
            String selectedName = modelNameSelected.getName().substring(0, modelNameSelected.getName().indexOf("_IDX"));
            long newID = createEntityModel(selectedName, modelPathSelected.getModelPath());
            Node newModel = (Node) base.getSpatialSystem().getSpatialControl(newID).getGeneralNode();
            newModel.setLocalTransform(selectedModel.getWorldTransform());

            // Clone data
            ConcurrentHashMap<String, String> dataOfSelected = base.getDataManager().getEntityData(idOfSelected);
            ConcurrentHashMap<String, String> dataNew = base.getDataManager().getEntityData(newID);
            for (String key : dataOfSelected.keySet()) {
                dataNew.put(key, dataOfSelected.get(key));
            }

            tempList.add(newID);
            layerToClone.attachChild(newModel);
        }

        // clear selection
        base.getSelectionManager().clearSelectionList();

        for (Long id : tempList) {
            base.getSelectionManager().selectEntity(id, EditorSelectionManager.SelectionMode.Additive);
        }
        base.getSelectionManager().calculateSelectionCenter();
        return tempList;
    }

    protected void removeEntityObject(long id) {
        // remove item from scene list
//        EntityNameComponent nameComp = (EntityNameComponent) base.getEntityManager().getComponent(id, EntityNameComponent.class);
//        base.getGuiManager().getSceneObjectsListBox().removeItem(nameComp.getName() + "(" + id + ")");

        //remove item from selection
        List<Long> selList = base.getSelectionManager().getSelectionList();
        if (selList.contains(id)) {
            selList.remove(id);
            Node nd = (Node) base.getSpatialSystem().getSpatialControl(id).getGeneralNode();
            base.getSelectionManager().removeSelectionBox(nd);
            nd = null;
        }

        // destroy entity
        base.getEntityManager().removeEntity(id);
        base.getSpatialSystem().removeSpatialControl(id);
        base.getDataManager().removeEntityData(id);
    }

    // Correct path for Windows OS
    protected String correctPath(String path) {
        String pathCorrected = path;

        if (File.separatorChar == '\\') {
            pathCorrected = pathCorrected.replace('\\', '/');
        }
        if (!path.endsWith("/") && path.indexOf(".") < 0) {
            pathCorrected += "/";
        }

        return pathCorrected;
    }

    protected JSONObject parseJsonFile(String path) {
        // Load JSON script
        JSONParser json = new JSONParser();

        FileReader fileRead = null;
        JSONObject jsObj = null;

        try {
            fileRead = new FileReader(new File(path));
        } catch (FileNotFoundException ex) {
            System.out.println("bad JSON file");
        }

        try {
            jsObj = (JSONObject) json.parse(fileRead);
        } catch (IOException ex) {
            System.out.println("bad JSON file");
        } catch (org.json.simple.parser.ParseException ex) {
            System.out.println("bad JSON file");
        }


        try {
            fileRead.close();
        } catch (IOException ex) {
            System.out.println("bad JSON file");
        }
        return jsObj;
    }

    protected Long addEntityToScene(String name) {
        return createEntityModel(name, entitiesList.get(name));
    }

    // Recursive search of files
    protected void findFiles(String dirEntity, String fileExtension) {
        System.out.println("ooooooooo LOAD entity Dir : " + dirEntity);
        File dir = new File(dirEntity);
        File[] a = dir.listFiles();

        for (File f : a) {
            if (f.isDirectory() && f.getName().indexOf("svn") < 0) {
                // Recursive search
                System.out.println("****** CHECKing Dir : " + f.getName());
                String recursDir = dirEntity + "/" + f.getName();
                findFiles(recursDir, fileExtension);
            } else if (f.getName().endsWith("." + fileExtension)) {

                String strF = f.getName();
                String modelName = f.getName().substring(0, f.getName().indexOf(".j3o"));
                String modelRelativePath = f.getAbsolutePath().substring(assetsList.get(assetsList.size() - 1).length(), f.getAbsolutePath().length());
                entitiesList.put(modelName, modelRelativePath);
//                strF = strF.substring(globalDirToFind.length(), strF.length());
                System.out.println("========>>FOUND ENTITY :: " + strF);
            }
        }
    }

    protected static List<String> getAssetsList() {
        return assetsList;
    }

    protected static ConcurrentHashMap<String, String> getEntitiesListsList() {
        return entitiesList;
    }
}
