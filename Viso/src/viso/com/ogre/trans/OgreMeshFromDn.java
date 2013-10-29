package viso.com.ogre.trans;

import java.util.List;

import viso.com.table.Table;

public class OgreMeshFromDn{
	
	public static Table convertTable(final Table ogrMesh, final Table dnmesh){
		Table submeshes = ogrMesh.MapAndCreateTable("submeshes");
		Table submeshArray = submeshes.MapAndCreateArray("submeshArray");
		Table dnmeshArray = dnmesh.getTable("meshInfoArray");
		List<Object> dnmesh_array = dnmeshArray.repeatElements();
		for(Object obj : dnmesh_array){
			if(!(obj instanceof Table)){
				throw new IllegalStateException(" obj 不是 table 类");
			}
			Table dnmeshInfo = (Table)obj;
			Table ogrMeshUnit = Table.createTable("submesh");
			submeshArray.PutObject(ogrMeshUnit);
			
			ogrMeshUnit.MapString("material", dnmeshInfo.getString("meshName"));
			ogrMeshUnit.MapString("usesharedvertices", "false");
			ogrMeshUnit.MapString("use32bitindexes", "false");
			ogrMeshUnit.MapString("operationtype", "triangle_list");
			
			{//face
				List<Object> dnIndexArray = dnmeshInfo.getTable("vertexindex").repeatElements();
				Table faces = ogrMeshUnit.MapAndCreateTable("faces");
				faces.MapInt("count", dnIndexArray.size());
				
				Table faceArray = faces.MapAndCreateArray("faceArray");
				for(int i=0;i<dnIndexArray.size();i++){
					faceArray.PutObject(dnIndexArray.get(i));
				}
			}
			{//顶点
				List<Object> UVData = dnmeshInfo.getTable("UVDataArray").repeatElements();
				List<Object> dnVertexData = dnmeshInfo.getTable("vertexDataArray").repeatElements();
				List<Object> dnNormalData = dnmeshInfo.getTable("normalDataArray").repeatElements();
				
				List<Object> vertex_table_list;
				List<Object> normal_table_list;
				List<Object> uvdata_table_list;
				
				Table geometry = ogrMeshUnit.MapAndCreateTable("geometry");
				geometry.MapInt("vertexcount", dnVertexData.size());
				
				Table vertexbuffer0 = geometry.MapAndCreateTable("vertexbuffer0");
				vertexbuffer0.MapString("positions", "true");
				vertexbuffer0.MapString("normals", "true");
				
				Table positionArray = vertexbuffer0.MapAndCreateArray("positionArray");
				Table normalArray = vertexbuffer0.MapAndCreateArray("normalArray");
				
				Table vertexbuffer1 = geometry.MapAndCreateTable("vertexbuffer1");
				vertexbuffer1.MapString("texture_coord_dimensions_0", "float2");
				vertexbuffer1.MapString("texture_coords", "1");
				
				Table vertexArray = vertexbuffer1.MapAndCreateArray("vertexArray");
				
				for(int i=0;i<dnVertexData.size();i++){
					
					vertex_table_list = ((Table)dnVertexData.get(i)).repeatElements();
					normal_table_list = ((Table)dnNormalData.get(i)).repeatElements();
					uvdata_table_list = ((Table)UVData.get(i)).repeatElements();
					
					positionArray.PutObject(vertex_table_list.get(0));
					positionArray.PutObject(vertex_table_list.get(1));
					positionArray.PutObject(vertex_table_list.get(2));
					
					normalArray.PutObject(normal_table_list.get(0));
					normalArray.PutObject(normal_table_list.get(1));
					normalArray.PutObject(normal_table_list.get(2));
					
					vertexArray.PutObject(uvdata_table_list.get(0));
					vertexArray.PutObject(uvdata_table_list.get(1));
					
				}
			}
			{//骨骼
				List<Object> dnBoneIndexArray = dnmeshInfo.getTable("boneIndexArray").repeatElements();
				List<Object> dnBeWeightArray = dnmeshInfo.getTable("boneWeightArray").repeatElements();
				
				Table boneassignments = ogrMeshUnit.MapAndCreateTable("boneassignments");
				Table vertexIndexArray = boneassignments.MapAndCreateArray("vertexIndexArray");
				Table boneIndexArray = boneassignments.MapAndCreateArray("boneIndexArray");
				Table weightArray = boneassignments.MapAndCreateArray("weightArray");
				
				List<Object> index_table_list;
				List<Object> weight_table_list;
				for(int i=0;i<dnBoneIndexArray.size();i++){
					index_table_list = ((Table)dnBoneIndexArray.get(i)).repeatElements();
					weight_table_list = ((Table)dnBeWeightArray.get(i)).repeatElements();
					
					for(int j=0;j<4;j++){
						vertexIndexArray.PutObject(new Integer(i));
						boneIndexArray.PutObject(index_table_list.get(j));
						weightArray.PutObject(weight_table_list.get(j));
					}
					
				}
				
			}
		}
		
		return ogrMesh;
	}
}
