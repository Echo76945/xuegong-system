package com.xuegong.config;

import com.xuegong.entity.*;
import com.xuegong.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;
    private final ScholarshipApplyRepository scholarshipRepository;
    private final UserRepository userRepository;
    private final FamilyInfoRepository familyInfoRepository;
    private final AnnouncementRepository announcementRepository;
    private final ActivityRepository activityRepository;
    private final LateRecordRepository lateRecordRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final IncidentRepository incidentRepository;
    private final ExtracurricularRepository extracurricularRepository;
    private final AwardRepository awardRepository;

    @Override
    public void run(String... args) {
        // 初始化用户数据
        userRepository.save(buildUser("admin", "123456", "张管理", "管理员", "13800000000", "admin@school.edu.cn"));
        userRepository.save(buildUser("teacher", "123456", "王教授", "教师", "13800001111", "wang@school.edu.cn"));
        userRepository.save(buildUser("student", "123456", "李同学", "学生", "13800002222", "li@stu.edu.cn"));

        // 初始化学生数据
        studentRepository.save(buildStudent("2024001", "张三", "男", "计算机2401", "13800001111", "在校"));
        studentRepository.save(buildStudent("2024002", "李四", "女", "计算机2401", "13800002222", "在校"));
        studentRepository.save(buildStudent("2024003", "王五", "男", "软件2401", "13800003333", "在校"));
        studentRepository.save(buildStudent("2024004", "赵六", "女", "软件2401", "13800004444", "离校"));
        studentRepository.save(buildStudent("2024005", "孙七", "男", "计算机2402", "13800005555", "在校"));

        // 初始化课程数据
        courseRepository.save(buildCourse("CS101", "计算机导论", "必修", 3.0, "王教授", "周一 3-4节", 60, 45));
        courseRepository.save(buildCourse("CS201", "数据结构", "必修", 4.0, "李教授", "周二 1-2节", 50, 48));
        courseRepository.save(buildCourse("CS301", "操作系统", "必修", 4.0, "张教授", "周三 5-6节", 45, 40));
        courseRepository.save(buildCourse("EL101", "音乐欣赏", "选修", 2.0, "陈老师", "周四 7-8节", 80, 62));
        courseRepository.save(buildCourse("EL201", "摄影入门", "选修", 2.0, "刘老师", "周五 3-4节", 40, 38));

        // 初始化成绩数据
        scoreRepository.save(buildScore("2024001", "张三", "计算机导论", 92.0, "2024-2025-1"));
        scoreRepository.save(buildScore("2024001", "张三", "数据结构", 88.0, "2024-2025-1"));
        scoreRepository.save(buildScore("2024002", "李四", "计算机导论", 95.0, "2024-2025-1"));
        scoreRepository.save(buildScore("2024003", "王五", "数据结构", 58.0, "2024-2025-1"));
        scoreRepository.save(buildScore("2024002", "李四", "音乐欣赏", 85.0, "2024-2025-2"));

        // 初始化奖助申请数据
        scholarshipRepository.save(buildApply("张三", "奖学金", "国家奖学金", 8000.0, "学习成绩优异，综合测评排名前列", "待班主任审批", "teacher", null, "2025-03-10"));
        scholarshipRepository.save(buildApply("李四", "助学金", "国家助学金", 3000.0, "家庭经济困难，需要资助完成学业", "待学校审批", "school", "", "2025-03-08"));
        scholarshipRepository.save(buildApply("王五", "奖学金", "校级一等奖学金", 5000.0, "本学期成绩排名前5%", "已驳回", null, "成绩不符合要求", "2025-03-05"));
        scholarshipRepository.save(buildApply("赵六", "勤工助学", "图书馆助理", 2000.0, "希望获得勤工助学岗位", "待班主任审批", "teacher", null, "2025-03-12"));

        // 初始化家庭情况数据
        familyInfoRepository.save(buildFamilyInfo("2024001", "张三", "张父", "父亲", "13900001111", "北京市海淀区xx路xx号", 12.0, "", ""));
        familyInfoRepository.save(buildFamilyInfo("2024002", "李四", "李母", "母亲", "13900002222", "河北省石家庄市xx街xx号", 2.5, "一般困难", "单亲家庭"));
        familyInfoRepository.save(buildFamilyInfo("2024003", "王五", "王父", "父亲", "13900003333", "山东省济南市xx路xx号", 8.0, "", ""));
        familyInfoRepository.save(buildFamilyInfo("2024004", "赵六", "赵母", "母亲", "13900004444", "河南省郑州市xx村", 1.2, "特别困难", "低保户，家中多人患病"));

        // 初始化公告数据
        announcementRepository.save(buildAnnouncement("关于2025-2026学年第一学期选课通知", "各位同学，新学期选课系统将于9月1日开放，请在规定时间内完成选课。必修课已自动选入，选修课需自行选择。", "全校", "教务处", 256, true));
        announcementRepository.save(buildAnnouncement("计算机学院暑期实习报名", "计算机学院2024级学生暑期实习报名已开始，合作企业包括华为、腾讯、阿里巴巴等，请有意向的同学在5月20日前提交申请。", "院系", "计算机学院", 128, false));
        announcementRepository.save(buildAnnouncement("计算机2401班班会通知", "本周五下午3点在A301教室召开班会，主题为期末复习规划，请全体同学准时参加。", "班级", "辅导员", 45, false));
        announcementRepository.save(buildAnnouncement("图书馆延长开放时间通知", "考试周期间图书馆开放时间延长至晚上10:30，请同学们合理安排学习时间。", "全校", "图书馆", 189, true));

        // 初始化活动数据
        activityRepository.save(buildActivity("校园歌手大赛", "一年一度的校园歌手大赛，展示你的音乐才华！", LocalDate.now().plusDays(15), "大学生活动中心", 200, 156, "报名中"));
        activityRepository.save(buildActivity("编程马拉松", "24小时编程挑战，组队参赛赢取丰厚奖品", LocalDate.now().plusDays(10), "计算机学院实验室", 60, 58, "报名中"));
        activityRepository.save(buildActivity("志愿者社区服务", "走进社区，关爱老人，传递温暖", LocalDate.now().plusDays(5), "阳光社区", 30, 30, "进行中"));
        activityRepository.save(buildActivity("春季运动会", "强身健体，展现青春风采", LocalDate.now().minusDays(30), "学校运动场", 500, 420, "已结束"));

        // 初始化晚归记录数据
        lateRecordRepository.save(buildLateRecord("2024001", "张三", "计算机2401", LocalDate.now(), "23:45", "图书馆自习", false));
        lateRecordRepository.save(buildLateRecord("2024003", "王五", "软件2401", LocalDate.now(), "01:20", "外出未归，电话联系不上", true));
        lateRecordRepository.save(buildLateRecord("2024005", "孙七", "计算机2402", LocalDate.now().minusDays(1), "23:10", "社团活动", false));
        lateRecordRepository.save(buildLateRecord("2024002", "李四", "计算机2401", LocalDate.now().minusDays(2), "00:30", "回家探亲，已请假", false));

        // 初始化外出报备数据
        leaveRequestRepository.save(buildLeaveRequest("张三", "计算机2401", "市中心医院", LocalDateTime.now().plusDays(1).withHour(8).withMinute(0), LocalDateTime.now().plusDays(1).withHour(17).withMinute(0), "身体不适，需前往医院就诊", "待审核"));
        leaveRequestRepository.save(buildLeaveRequest("李四", "计算机2401", "老家", LocalDateTime.now().plusDays(2).withHour(12).withMinute(0), LocalDateTime.now().plusDays(4).withHour(18).withMinute(0), "家中有急事需要回家处理", "已通过"));
        leaveRequestRepository.save(buildLeaveRequest("王五", "软件2401", "科技园区", LocalDateTime.now().withHour(14).withMinute(0), LocalDateTime.now().withHour(18).withMinute(0), "参加企业面试", "已通过"));
        leaveRequestRepository.save(buildLeaveRequest("赵六", "软件2401", "火车站", LocalDateTime.now().plusDays(5).withHour(6).withMinute(0), LocalDateTime.now().plusDays(5).withHour(9).withMinute(0), "赶火车回家", "已驳回"));

        // 初始化异常上报数据
        incidentRepository.save(buildIncident("3号宿舍楼消防通道被杂物堵塞", "张三", "安全", "紧急", "3号楼2层消防通道堆放大量杂物，存在严重安全隐患。", "处理中", "后勤处"));
        incidentRepository.save(buildIncident("教学楼A区饮水机故障", "李四", "设备", "一般", "A区3楼饮水机不出水，已持续2天。", "待处理", ""));
        incidentRepository.save(buildIncident("学生心理异常预警", "辅导员王老师", "心理", "重要", "计算机2401班赵六近期情绪低落，多次缺课，需关注。", "已处理", "心理中心"));
        incidentRepository.save(buildIncident("运动场地面破损", "体育部", "安全", "一般", "篮球场3号场地地面有裂缝，容易导致扭伤。", "已处理", "后勤处"));

        // 初始化第二课堂数据
        extracurricularRepository.save(buildExtracurricular("张三", "校园马拉松志愿服务", "志愿服务", 10, 8.0, LocalDate.now().minusMonths(6), "已认定"));
        extracurricularRepository.save(buildExtracurricular("张三", "编程社团技术分享", "社团活动", 5, 2.0, LocalDate.now().minusMonths(7), "已认定"));
        extracurricularRepository.save(buildExtracurricular("李四", "暑期三下乡社会实践", "社会实践", 20, 40.0, LocalDate.now().minusMonths(10), "已认定"));
        extracurricularRepository.save(buildExtracurricular("王五", "校运动会400米跑", "文体活动", 8, 3.0, LocalDate.now().minusMonths(7), "已认定"));
        extracurricularRepository.save(buildExtracurricular("李四", "数学建模校赛", "学科竞赛", 15, 24.0, LocalDate.now().minusMonths(8), "待认定"));

        // 初始化评奖评优数据
        awardRepository.save(buildAward("张三", "国家奖学金", "奖学金", "国家级", "教育部", LocalDate.now().minusMonths(5), 8000.0));
        awardRepository.save(buildAward("李四", "ACM程序设计竞赛金奖", "竞赛", "国家级", "ACM组委会", LocalDate.now().minusMonths(6), 5000.0));
        awardRepository.save(buildAward("王五", "校级三好学生", "荣誉", "校级", "学校学工处", LocalDate.now().minusMonths(7), 0.0));
        awardRepository.save(buildAward("张三", "省级数学建模二等奖", "竞赛", "省级", "省教育厅", LocalDate.now().minusMonths(8), 2000.0));
        awardRepository.save(buildAward("赵六", "校级一等奖学金", "奖学金", "校级", "学校财务处", LocalDate.now().minusMonths(5), 3000.0));
    }

    private User buildUser(String username, String password, String name, String role, String phone, String email) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setName(name);
        u.setRole(role);
        u.setPhone(phone);
        u.setEmail(email);
        u.setStatus("启用");
        return u;
    }

    private Student buildStudent(String code, String name, String gender, String className, String phone, String status) {
        Student s = new Student();
        s.setCode(code);
        s.setName(name);
        s.setGender(gender);
        s.setClassName(className);
        s.setPhone(phone);
        s.setStatus(status);
        return s;
    }

    private Course buildCourse(String code, String name, String type, Double credit, String teacher, String schedule, int capacity, int enrolled) {
        Course c = new Course();
        c.setCode(code);
        c.setName(name);
        c.setType(type);
        c.setCredit(credit);
        c.setTeacher(teacher);
        c.setSchedule(schedule);
        c.setCapacity(capacity);
        c.setEnrolled(enrolled);
        return c;
    }

    private Score buildScore(String studentCode, String studentName, String courseName, Double score, String semester) {
        Score s = new Score();
        s.setStudentCode(studentCode);
        s.setStudentName(studentName);
        s.setCourseName(courseName);
        s.setScore(score);
        s.setSemester(semester);
        return s;
    }

    private ScholarshipApply buildApply(String studentName, String type, String name, Double amount, String reason, String status, String approvalStage, String approverRemark, String date) {
        ScholarshipApply a = new ScholarshipApply();
        a.setStudentName(studentName);
        a.setType(type);
        a.setName(name);
        a.setAmount(amount);
        a.setReason(reason);
        a.setStatus(status);
        a.setApprovalStage(approvalStage);
        a.setApproverRemark(approverRemark);
        a.setApplyTime(LocalDate.parse(date));
        return a;
    }

    private FamilyInfo buildFamilyInfo(String studentCode, String studentName, String parentName, String relation, String parentPhone, String address, Double income, String difficulty, String remark) {
        FamilyInfo f = new FamilyInfo();
        f.setStudentCode(studentCode);
        f.setStudentName(studentName);
        f.setParentName(parentName);
        f.setRelation(relation);
        f.setParentPhone(parentPhone);
        f.setAddress(address);
        f.setIncome(income);
        f.setDifficulty(difficulty);
        f.setRemark(remark);
        return f;
    }

    private Announcement buildAnnouncement(String title, String content, String scope, String author, Integer views, Boolean pinned) {
        Announcement a = new Announcement();
        a.setTitle(title);
        a.setContent(content);
        a.setScope(scope);
        a.setAuthor(author);
        a.setViews(views);
        a.setPinned(pinned);
        return a;
    }

    private Activity buildActivity(String name, String description, LocalDate date, String location, Integer capacity, Integer enrolled, String status) {
        Activity a = new Activity();
        a.setName(name);
        a.setDescription(description);
        a.setDate(date);
        a.setLocation(location);
        a.setCapacity(capacity);
        a.setEnrolled(enrolled);
        a.setStatus(status);
        return a;
    }

    private LateRecord buildLateRecord(String studentCode, String studentName, String className, LocalDate date, String returnTime, String reason, Boolean abnormal) {
        LateRecord r = new LateRecord();
        r.setStudentCode(studentCode);
        r.setStudentName(studentName);
        r.setClassName(className);
        r.setDate(date);
        r.setReturnTime(returnTime);
        r.setReason(reason);
        r.setAbnormal(abnormal);
        return r;
    }

    private LeaveRequest buildLeaveRequest(String studentName, String className, String destination, LocalDateTime startTime, LocalDateTime endTime, String reason, String status) {
        LeaveRequest r = new LeaveRequest();
        r.setStudentName(studentName);
        r.setClassName(className);
        r.setDestination(destination);
        r.setStartTime(startTime);
        r.setEndTime(endTime);
        r.setReason(reason);
        r.setStatus(status);
        return r;
    }

    private Incident buildIncident(String title, String reporter, String category, String severity, String description, String status, String handler) {
        Incident i = new Incident();
        i.setTitle(title);
        i.setReporter(reporter);
        i.setCategory(category);
        i.setSeverity(severity);
        i.setDescription(description);
        i.setStatus(status);
        i.setHandler(handler);
        return i;
    }

    private Extracurricular buildExtracurricular(String studentName, String activityName, String category, Integer points, Double duration, LocalDate date, String status) {
        Extracurricular e = new Extracurricular();
        e.setStudentName(studentName);
        e.setActivityName(activityName);
        e.setCategory(category);
        e.setPoints(points);
        e.setDuration(duration);
        e.setDate(date);
        e.setStatus(status);
        return e;
    }

    private Award buildAward(String studentName, String awardName, String type, String level, String grantUnit, LocalDate awardDate, Double bonus) {
        Award a = new Award();
        a.setStudentName(studentName);
        a.setAwardName(awardName);
        a.setType(type);
        a.setLevel(level);
        a.setGrantUnit(grantUnit);
        a.setAwardDate(awardDate);
        a.setBonus(bonus);
        return a;
    }
}
