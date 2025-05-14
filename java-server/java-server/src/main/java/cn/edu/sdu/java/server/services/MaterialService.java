package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Material;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.models.User;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.MyTreeNode;
import cn.edu.sdu.java.server.repositorys.MaterialRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.repositorys.UserRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    @Value("${attach.folder}")    //环境配置变量获取
    private String attachFolder;  //服务器端数据存储

    // 获取文件目录树（根目录）
    public List<MyTreeNode> getRootMaterials(@Valid DataRequest dataRequest) {
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        List<Material> sList = materialRepository.findRootList();// 根节点没有filename
        if(sList == null)
            return childList;
        for (Material m : sList) {
            childList.add(getMaterialTreeNode(null, m, null));
        }
        return childList;
    }

    public MyTreeNode getMaterialTreeNode( Integer pid, Material m,String parentTitle) {
        MyTreeNode  node = new MyTreeNode(m.getMaterialId(),m.getCourseName(),m.getTitle(),null);
        node.setLabel(m.getCourseName()+"-"+m.getTitle());
        node.setParentTitle(parentTitle);
        node.setPid(pid);
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        node.setChildren(childList);
        List<Material> sList = materialRepository.findByParent(m);// 找子节点
        if(sList == null)
            return node;
        for (Material material : sList) {
            childList.add(getMaterialTreeNode(node.getId(), material, node.getValue()));
        }
        return node;
    }

    // 获取某个目录下的文件
    public DataResponse getMaterialsByParent(@Valid DataRequest dataRequest) {

        return null;
    }

    // 获取文件或目录
    public DataResponse getMaterialById(@Valid DataRequest dataRequest) {

        return null;
    }

    public DataResponse uploadFile(byte[] barr, String remoteFile, String uploader, String fileName, String id, String courseName, String title, String pid) {
        try {
            File fullPath = new File(attachFolder + remoteFile);

            // 先确保目录存在
            File parentDir = fullPath.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();  // 自动创建所有父目录
            }
            // 输出路径
            OutputStream os = new FileOutputStream(new File(attachFolder + remoteFile));

            os.write(barr);
            os.close();
            // 根据UserId获取personId然后获取TeacherId
            User user = userRepository.findByUserId(Integer.parseInt(uploader));
            Integer personId = user.getPerson().getPersonId();
            Optional<Teacher> byPersonPersonId = teacherRepository.findByPersonPersonId(personId);

            Teacher teacher = null;
            if (byPersonPersonId.isPresent()) {
                teacher = byPersonPersonId.get();
            }
            // 验证资料是否存在
            Optional<Material> mat = materialRepository.findById(Integer.parseInt(id));
            if (mat.isPresent()) {
                return CommonMethod.getReturnMessageError("一个节点只能有一个资料!");
            }
            // 存到数据库
            Material material = new Material();
            material.setTeacher(teacher);
            material.setMaterialId(Integer.parseInt(id)); // 手动添加id就得注意实体类的注解别写错
            material.setCourseName(courseName);
            material.setTitle(title);
            material.setFileName(remoteFile);
            if(pid == null){
                return CommonMethod.getReturnMessageError("请选择父节点下的子节点");
            }
            Optional<Material> m = materialRepository.findById(Integer.parseInt(pid));
            Material pMaterial = null;
            if (m.isPresent()) {
                pMaterial = m.get();
            }
            material.setParent(pMaterial);

            materialRepository.save(material);

            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            // return CommonMethod.getReturnMessageError("上传错误");
            throw  new RuntimeException(e.getMessage());
        }
    }

    private String saveFileToServer(MultipartFile file) throws IOException {

        return null;
    }

    // 修改文件或文件夹的标题
    public DataResponse updateMaterialTitle(@Valid DataRequest dataRequest) {

        return null;
    }

    // 删除文件或文件夹
    public DataResponse deleteMaterial(@Valid DataRequest dataRequest) {

        return null;
    }

    public DataResponse getFileByteData(DataRequest dataRequest) {

        return null;
    }


}
