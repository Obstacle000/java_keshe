package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.HonorRepository;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HonorService {
    @Autowired
    private HonorRepository honorRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private PersonRepository personRepository;

    public DataResponse getHonorList(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if(personId == null)
            personId = 0;


        List<Honor> hList = honorRepository.findByPersonPersonId(personId);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Honor h : hList) {
            m = new HashMap<>();
            m.put("honorId", h.getHonor()+"");
            m.put("personId",h.getPerson().getPersonId()+"");
            // 上面的是顺便返回,下面的是要展示的数据
            m.put("num",h.getPerson().getNum());
            m.put("name",h.getPerson().getName());
            String num = h.getPerson().getNum();
            Optional<Student> studentOP = studentRepository.findByPersonNum(num);
            if(studentOP.isPresent()){
                Student student = studentOP.get();
                m.put("className",student.getClassName());
            }
            m.put("honor",""+h.getHonor());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse honorSave(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        String honor = dataRequest.getString("honor");
        Integer honorId = dataRequest.getInteger("honorId");
        Optional<Honor> op;
        Honor h = null;
        if(honorId != null) {
            op= honorRepository.findById(honorId);
            if(op.isPresent())
                h = op.get();
        }
        if(h == null) {
            h = new Honor();
            h.setPerson(personRepository.findById(personId).get());

        }
        h.setHonor(honor);
        honorRepository.save(h);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse honorDelete(@Valid DataRequest dataRequest) {
        Integer honorId = dataRequest.getInteger("honorId");
        Optional<Honor> op;
        Honor h = null;
        if(honorId != null) {
            op= honorRepository.findById(honorId);
            if(op.isPresent()) {
                h = op.get();
                honorRepository.delete(h);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public OptionItemList getPersonItemOptionList(@Valid DataRequest dataRequest) {
        // 查询所有
        List<Person> pList = personRepository.findPersonListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Person p : pList) {
            // 返回格式
            itemList.add(new OptionItem( p.getPersonId(),p.getPersonId()+"",p.getNum()+"-"+p.getName()));
        }
        return new OptionItemList(0, itemList);
    }
}
