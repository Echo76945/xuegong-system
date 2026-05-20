package com.xuegong.ai.langchain;

import com.xuegong.entity.Student;
import com.xuegong.service.StudentService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(name = "openai.api-key")
@RequiredArgsConstructor
public class StudentInfoTool {

    private final StudentService studentService;

    @Tool("查询学生信息，根据学生姓名或学号搜索")
    public String searchStudent(String keyword) {
        log.info("AI 工具调用: 搜索学生 - {}", keyword);
        List<Student> students = studentService.search(keyword);
        if (students.isEmpty()) {
            return "未找到匹配的学生信息";
        }
        return students.stream()
                .limit(5)
                .map(s -> String.format("学号: %s, 姓名: %s, 性别: %s, 班级: %s, 状态: %s",
                        s.getCode(), s.getName(), s.getGender(), s.getClassName(), s.getStatus()))
                .collect(Collectors.joining("\n"));
    }

    @Tool("获取所有班级列表")
    public String getClassNames() {
        log.info("AI 工具调用: 获取班级列表");
        List<String> classNames = studentService.findAllClassNames();
        return "班级列表: " + String.join(", ", classNames);
    }

    @Tool("统计学生数量")
    public String countStudents() {
        log.info("AI 工具调用: 统计学生数量");
        List<Student> students = studentService.findAll();
        return "当前系统共有 " + students.size() + " 名学生";
    }
}
