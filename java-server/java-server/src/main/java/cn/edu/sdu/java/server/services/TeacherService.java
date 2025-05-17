package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private SystemService systemService;


    // 从数据库里拿出来的是很多的List,得转换成map
    public Map<String,Object> getMapFromTeacher(Teacher t) {
        Map<String,Object> m = new HashMap<>();
        Person p;
        if(t == null)
            return m;
        m.put("major",t.getMajor());
        m.put("title",t.getTitle());
        m.put("degree",t.getDegree());
        p = t.getPerson();
        if(p == null)
            return m;
        m.put("personId", t.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        return m;
    }
    // 根据学号拿到存了老师的map的集合
    public List<Map<String,Object>> getTeacherMapList(String numName) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.isEmpty())
            return dataList;
        for (Teacher teacher : sList) {
            dataList.add(getMapFromTeacher(teacher));
        }
        return dataList;
    }
    // 拿到list
    public DataResponse getTeacherList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @Transactional
    public DataResponse teacherDelete(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");  //获取student_id值
        Teacher t = null;
        Optional<Teacher> op;
        if (personId != null && personId > 0) {
            op = teacherRepository.findById(personId);   //查询获得实体对象
            if(op.isPresent()) {
                t = op.get();
                Optional<User> uOp = userRepository.findByPerson(t.getPerson()); //查询对应该学生的账户
                //删除对应该学生的账户
                uOp.ifPresent(userRepository::delete);
                Person p = t.getPerson();
                teacherRepository.delete(t);    //首先数据库永久删除学生信息
                personRepository.delete(p);   // 然后数据库永久删除学生信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Teacher t = null;
        Optional<Teacher> op;
        if (personId != null) {
            op = teacherRepository.findById(personId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                t = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromTeacher(t)); //这里回传包含学生信息的Map对象
    }
    @Transactional
    public DataResponse teacherEditSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Map<String,Object> form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        Teacher t= null;
        Person p;
        User u;
        Optional<Teacher> op;
        boolean isNew = false;
        if (personId != null) {
            op = teacherRepository.findById(personId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                t = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if (nOp.isPresent()) {
            if (t == null || !t.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新学号已经存在，不能添加或修改！");
            }
        }
        if (t == null) {
            p = new Person();
            p.setNum(num);
            p.setType("1");
            personRepository.saveAndFlush(p);  //插入新的Person记录
            personId = p.getPersonId();
            String password = encoder.encode("123456");
            u = new User();
            u.setPerson(p);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_STUDENT));
            u.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            u.setCreatorId(CommonMethod.getPersonId());
            userRepository.saveAndFlush(u); //插入新的User记录
            t = new Teacher();   // 创建实体对象
            t.setPerson(p);
            try {
                teacherRepository.saveAndFlush(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isNew = true;
        } else {
            p = t.getPerson();
        }
        personId = p.getPersonId();
        if (!num.equals(p.getNum())) {   //如果人员编号变化，修改人员编号和登录账号
            Optional<User> uOp = userRepository.findByPersonPersonId(personId);
            if (uOp.isPresent()) {
                u = uOp.get();
                u.setUserName(num);
                userRepository.saveAndFlush(u);
            }
            p.setNum(num);  //设置属性
        }
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setCard(CommonMethod.getString(form, "card"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setBirthday(CommonMethod.getString(form, "birthday"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        personRepository.save(p);  // 修改保存人员信息
        t.setMajor(CommonMethod.getString(form, "major"));
        t.setTitle(CommonMethod.getString(form, "title"));
        t.setDegree(CommonMethod.getString(form, "degree"));
        teacherRepository.save(t);  //修改保存学生信息
        systemService.modifyLog(t,isNew);
        return CommonMethod.getReturnData(t.getPersonId());  // 将personId返回前端

    }

    @Transactional
    public DataResponse importTeacherExcel(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowNum = sheet.getLastRowNum();

            for (int i = 1; i <= rowNum; i++) { // 跳过表头
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Person person = new Person();
                person.setNum(getCellStringValue(row, 1));
                person.setName(getCellStringValue(row, 2));
                person.setDept(getCellStringValue(row, 3));
                person.setCard(getCellStringValue(row, 6));
                person.setGender("男".equals(getCellStringValue(row, 7)) ? "1" : "2");
                person.setBirthday(getCellStringValue(row, 8));
                person.setEmail(getCellStringValue(row, 9));
                person.setPhone(getCellStringValue(row, 10));
                person.setAddress(getCellStringValue(row, 11));
                person.setType("2"); // 老师类型为2
                personRepository.save(person);

                User user = new User();
                user.setUserName(person.getNum());
                user.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("123456"));
                user.setCreateTime(LocalDateTime.now().toString());
                user.setLoginCount(0);
                user.setPerson(person);
                user.setUserType(userTypeRepository.findByName(EUserType.ROLE_TEACHER)); // 老师角色
                userRepository.save(user);

                Teacher teacher = new Teacher();
                teacher.setPerson(person);
                teacher.setTitle(getCellStringValue(row, 12));
                teacher.setDegree(getCellStringValue(row, 13));
                teacher.setMajor(getCellStringValue(row, 14));
                teacherRepository.save(teacher);
            }
            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonMethod().getReturnMessageError("导入失败");
        }
    }

    /**
     * 读取单元格字符串内容，避免空指针异常
     */
    private String getCellStringValue(Row row, int cellIndex) {
        if (row == null) return "";
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

}
