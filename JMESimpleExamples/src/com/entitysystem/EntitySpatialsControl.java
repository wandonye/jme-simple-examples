/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.entitysystem;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Transform;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mifth
 */
public class EntitySpatialsControl extends AbstractControl {

    private static Spatial spatial;
    private static List<Geometry> mapChildMeshes = new ArrayList<Geometry>(); //Collection of meshes
    private static SpatialType type;
//    private static EntityManager entManager;
    private static long ID;
    private static ComponentsControl components;
    private static ComponentsUpdater updater;

    public EntitySpatialsControl(Spatial sp, long ID, ComponentsControl components) {

        this.ID = ID;
        this.components = components;
        spatial = sp;
        spatial.addControl(this);
        updater = new ComponentsUpdater(components);

    }

    public static enum SpatialType {
        Node,
        LightNode,
        BatchNode,
        CameraNode,
        GuiNode
    }    
    
    public static void setType(SpatialType type) {
        if (type.equals(SpatialType.Node)) type = SpatialType.Node;
        else if (type.equals(SpatialType.BatchNode)) type = SpatialType.BatchNode;
        else if (type.equals(SpatialType.CameraNode)) type = SpatialType.CameraNode;
        else if (type.equals(SpatialType.GuiNode)) type = SpatialType.GuiNode;
        else if (type.equals(SpatialType.LightNode)) type = SpatialType.LightNode;
    }
    
    public static SpatialType getType() {
        return type;
    }
    
    public static Spatial setGeneralNode(Spatial sp) {
        return spatial = sp;
    }
    
    public static Spatial getGeneralNode() {
        return spatial;
    }    
    
    //Read the node child to find geomtry and stored it to the map for later access as submesh
    public static void recurseNode(){
        Node nd_temp = (Node) spatial;
        nd_temp.setUserData("EntityID", ID);
        
        for (int i = 0; i < nd_temp.getChildren().size(); i++){
            
           if(nd_temp.getChildren().get(i) instanceof Node){
               nd_temp.getChildren().get(i).setUserData("EntityID", ID);
               recurseNode();
           }
           else if (nd_temp.getChildren().get(i) instanceof Geometry){
            Geometry geom = (Geometry) nd_temp.getChildren().get(i);
            geom.setUserData("EntityID", ID);
            //System.out.println("omomomomoomomomo GEOMETRY ADDED : "+geom.getName()+" for Entity "+mObjectName);
            mapChildMeshes.add(geom);
           }
        }
    }

    public static Geometry getChildMesh(String name){
        for (Geometry mc : mapChildMeshes) {
            if(name.equals(mc.getName())){
                return mc;
            }
        }
        return null;
    }
    
    public static List<Geometry> getChildMeshes(){
        return mapChildMeshes;
    }
    
    public void destroy() {
        mapChildMeshes.clear();
        spatial.removeFromParent();
        spatial.removeControl(this);
        spatial = null;
    }

    
    @Override
    protected void controlUpdate(float tpf) {
        
        if (updater.getDoUpdate()) {
        spatial.setLocalTransform(updater.getUpdateTransform());
        }
        System.out.println(ID);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }    
    
}