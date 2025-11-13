// QuestionGeneratorTool.java
package com.mathpractice.util;

import com.mathpractice.dto.QuestionGenerationRequest;
import com.mathpractice.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 创建题目工具类
 *
 * @author chidao
 * @since 2025-11-07
 */
@Slf4j
@Component
public class QuestionGeneratorTool {

    private final Random random = new Random();

    /**
     * 生成题目列表
     */
    public List<Question> generateQuestions(QuestionGenerationRequest request) {
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < request.getCount(); i++) {
            try {
                String type = getRandomType(request.getTypes());
                String difficulty = getRandomDifficulty(request.getDifficulties());

                Question question = generateSingleQuestion(type, difficulty, request.getNumberRange());
                question.setCreatedBy(request.getCreatedBy());

                questions.add(question);
            } catch (Exception e) {
                log.error("生成题目失败: {}", e.getMessage());
            }
        }

        log.info("成功生成 {} 道题目", questions.size());
        return questions;
    }

    /**
     * 生成单个题目
     */
    private Question generateSingleQuestion(String type, String difficulty,
                                            QuestionGenerationRequest.NumberRange range) {
        Question question = new Question();
        question.setType(type);
        question.setDifficulty(difficulty);

        switch (type) {
            case "AddAndSub":
                generateAddSubQuestion(question, difficulty, range);
                break;
            case "MulAndDiv":
                generateMulDivQuestion(question, difficulty, range);
                break;
            case "Mixed":
                generateMixedQuestion(question, difficulty, range);
                break;
            default:
                throw new IllegalArgumentException("不支持的题目类型: " + type);
        }

        return question;
    }

    /**
     * 生成加减法题目
     */
    private void generateAddSubQuestion(Question question, String difficulty,
                                        QuestionGenerationRequest.NumberRange range) {
        int num1, num2, num3;
        String content;
        BigDecimal answer;

        switch (difficulty) {
            case "easy":
                // 简单：两位数加减法
                num1 = getRandomNumber(range.getAddSubMin(), 50);
                num2 = getRandomNumber(range.getAddSubMin(), 50);
                if (random.nextBoolean()) {
                    content = num1 + " + " + num2 + " = ?";
                    answer = BigDecimal.valueOf(num1 + num2);
                } else {
                    // 确保减法结果为正数
                    int max = Math.max(num1, num2);
                    int min = Math.min(num1, num2);
                    content = max + " - " + min + " = ?";
                    answer = BigDecimal.valueOf(max - min);
                }
                break;

            case "medium":
                // 中等：三位数加减法或连加减
                if (random.nextBoolean()) {
                    num1 = getRandomNumber(100, range.getAddSubMax());
                    num2 = getRandomNumber(50, 150);
                    if (random.nextBoolean()) {
                        content = num1 + " + " + num2 + " = ?";
                        answer = BigDecimal.valueOf(num1 + num2);
                    } else {
                        content = num1 + " - " + num2 + " = ?";
                        answer = BigDecimal.valueOf(num1 - num2);
                    }
                } else {
                    // 连加减
                    num1 = getRandomNumber(10, 100);
                    num2 = getRandomNumber(1, 50);
                    num3 = getRandomNumber(1, 50);
                    content = num1 + " + " + num2 + " - " + num3 + " = ?";
                    answer = BigDecimal.valueOf(num1 + num2 - num3);
                }
                break;

            case "hard":
                // 困难：多位数复杂加减
                num1 = getRandomNumber(200, range.getAddSubMax());
                num2 = getRandomNumber(100, 200);
                num3 = getRandomNumber(50, 150);

                if (random.nextBoolean()) {
                    content = num1 + " + " + num2 + " - " + num3 + " = ?";
                    answer = BigDecimal.valueOf(num1 + num2 - num3);
                } else {
                    int num4 = getRandomNumber(1, 100);
                    content = num1 + " - " + num2 + " + " + num3 + " - " + num4 + " = ?";
                    answer = BigDecimal.valueOf(num1 - num2 + num3 - num4);
                }
                break;

            default:
                throw new IllegalArgumentException("不支持的难度: " + difficulty);
        }

        question.setContent(content);
        question.setAnswer(answer);
    }

    /**
     * 生成乘除法题目
     */
    private void generateMulDivQuestion(Question question, String difficulty,
                                        QuestionGenerationRequest.NumberRange range) {
        int num1, num2;
        String content;
        BigDecimal answer;

        switch (difficulty) {
            case "easy":
                // 简单：表内乘除法
                num1 = getRandomNumber(range.getMultiplicationMin(), 9);
                num2 = getRandomNumber(range.getMultiplicationMin(), 9);
                if (random.nextBoolean()) {
                    content = num1 + " × " + num2 + " = ?";
                    answer = BigDecimal.valueOf(num1 * num2);
                } else {
                    // 确保除法能整除
                    int product = num1 * num2;
                    content = product + " ÷ " + num1 + " = ?";
                    answer = BigDecimal.valueOf(num2);
                }
                break;

            case "medium":
                // 中等：两位数乘除法
                num1 = getRandomNumber(2, range.getMultiplicationMax());
                num2 = getRandomNumber(10, 20);
                if (random.nextBoolean()) {
                    content = num1 + " × " + num2 + " = ?";
                    answer = BigDecimal.valueOf(num1 * num2);
                } else {
                    // 确保除法能整除
                    int dividend = num1 * num2;
                    content = dividend + " ÷ " + num1 + " = ?";
                    answer = BigDecimal.valueOf(num2);
                }
                break;

            case "hard":
                // 困难：复杂乘除法或连乘除
                if (random.nextBoolean()) {
                    num1 = getRandomNumber(10, range.getMultiplicationMax());
                    num2 = getRandomNumber(10, range.getMultiplicationMax());
                    content = num1 + " × " + num2 + " = ?";
                    answer = BigDecimal.valueOf(num1 * num2);
                } else {
                    // 连乘除
                    num1 = getRandomNumber(2, 10);
                    num2 = getRandomNumber(2, 10);
                    int num3 = getRandomNumber(2, 10);
                    content = num1 + " × " + num2 + " ÷ " + num3 + " = ?";
                    // 确保能整除
                    int temp = num1 * num2;
                    if (temp % num3 != 0) {
                        // 如果不能整除，调整数字
                        num3 = findDivisor(temp);
                    }
                    answer = BigDecimal.valueOf(temp / (double) num3).setScale(2, RoundingMode.HALF_UP);
                }
                break;

            default:
                throw new IllegalArgumentException("不支持的难度: " + difficulty);
        }

        question.setContent(content);
        question.setAnswer(answer);
    }

    /**
     * 生成混合运算题目
     */
    private void generateMixedQuestion(Question question, String difficulty,
                                       QuestionGenerationRequest.NumberRange range) {
        String content;
        BigDecimal answer;

        switch (difficulty) {
            case "easy":
                // 简单：加减乘除混合，无括号
                content = generateEasyMixed(range);
                answer = calculateExpression(content.replace(" = ?", ""));
                break;

            case "medium":
                // 中等：带括号的混合运算
                content = generateMediumMixed(range);
                answer = calculateExpression(content.replace(" = ?", ""));
                break;

            case "hard":
                // 困难：复杂混合运算，多层括号
                content = generateHardMixed(range);
                answer = calculateExpression(content.replace(" = ?", ""));
                break;

            default:
                throw new IllegalArgumentException("不支持的难度: " + difficulty);
        }

        question.setContent(content);
        question.setAnswer(answer);
    }

    /**
     * 生成简单混合运算
     */
    private String generateEasyMixed(QuestionGenerationRequest.NumberRange range) {
        int num1 = getRandomNumber(1, 10);
        int num2 = getRandomNumber(1, 10);
        int num3 = getRandomNumber(1, 10);

        String[] operators = {"+", "-", "×", "÷"};
        String op1 = operators[random.nextInt(2)]; // 只取 + 或 -
        String op2 = operators[random.nextInt(2) + 2]; // 只取 × 或 ÷

        // 确保运算顺序正确
        if ("×".equals(op2) || "÷".equals(op2)) {
            return num1 + " " + op1 + " " + num2 + " " + op2 + " " + num3 + " = ?";
        } else {
            return num1 + " " + op2 + " " + num2 + " " + op1 + " " + num3 + " = ?";
        }
    }

    /**
     * 生成中等混合运算（带括号）
     */
    private String generateMediumMixed(QuestionGenerationRequest.NumberRange range) {
        int num1 = getRandomNumber(1, 30);
        int num2 = getRandomNumber(1, 20);
        int num3 = getRandomNumber(1, 15);

        String[] operators = {"+", "-", "×", "÷"};

        if (random.nextBoolean()) {
            return "(" + num1 + " + " + num2 + ") × " + num3 + " = ?";
        } else {
            return "(" + num1 + " - " + num2 + ") × " + num3 + " = ?";
        }
    }

    /**
     * 生成困难混合运算
     */
    private String generateHardMixed(QuestionGenerationRequest.NumberRange range) {
        int num1 = getRandomNumber(10, 50);
        int num2 = getRandomNumber(5, 30);
        int num3 = getRandomNumber(2, 20);
        int num4 = getRandomNumber(2, 10);

        if (random.nextBoolean()) {
            return "(" + num1 + " + " + num2 + ") × (" + num3 + " - " + num4 + ") = ?";
        } else {
            return "(" + num1 + " × " + num2 + ") ÷ (" + num3 + " + " + num4 + ") = ?";
        }
    }

    /**
     * 计算表达式结果
     */
    private BigDecimal calculateExpression(String expression) {
        try {
            // 替换数学符号为Java可识别的运算符
            String javaExpr = expression.replace("×", "*").replace("÷", "/");

            // 简单的表达式计算（实际项目中可以使用成熟的表达式计算库）
            if (javaExpr.contains("(")) {
                // 处理括号表达式
                return evaluateWithBrackets(javaExpr);
            } else {
                // 简单表达式直接计算
                return evaluateSimpleExpression(javaExpr);
            }
        } catch (Exception e) {
            log.error("计算表达式失败: {}", expression, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算带括号的表达式
     */
    private BigDecimal evaluateWithBrackets(String expression) {
        // 简化实现：实际项目中建议使用 ScriptEngine 或 exp4j 等库
        try {
            // 移除所有空格
            expression = expression.replaceAll("\\s+", "");

            // 这里使用JavaScript引擎计算（需要确保环境支持）
            javax.script.ScriptEngineManager manager = new javax.script.ScriptEngineManager();
            javax.script.ScriptEngine engine = manager.getEngineByName("JavaScript");

            Object result = engine.eval(expression);
            if (result instanceof Number) {
                return BigDecimal.valueOf(((Number) result).doubleValue()).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("计算带括号表达式失败: {}", expression, e);
            // 备用方案：手动计算简单括号表达式
            return evaluateSimpleBrackets(expression);
        }
    }

    /**
     * 手动计算简单括号表达式（备用方案）
     */
    private BigDecimal evaluateSimpleBrackets(String expression) {
        // 简化实现，只处理简单情况
        try {
            if (expression.contains("(") && expression.contains(")")) {
                String inner = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")"));
                BigDecimal innerResult = evaluateSimpleExpression(inner);

                String remaining = expression.substring(expression.indexOf(")") + 1);
                if (remaining.startsWith("*")) {
                    BigDecimal right = evaluateSimpleExpression(remaining.substring(1));
                    return innerResult.multiply(right);
                } else if (remaining.startsWith("/")) {
                    BigDecimal right = evaluateSimpleExpression(remaining.substring(1));
                    return innerResult.divide(right, 2, RoundingMode.HALF_UP);
                }
            }
            return evaluateSimpleExpression(expression);
        } catch (Exception e) {
            log.error("手动计算括号表达式失败: {}", expression, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算简单表达式
     */
    private BigDecimal evaluateSimpleExpression(String expression) {
        try {
            String[] parts = expression.split(" ");
            BigDecimal result = new BigDecimal(parts[0]);

            for (int i = 1; i < parts.length; i += 2) {
                String operator = parts[i];
                BigDecimal next = new BigDecimal(parts[i + 1]);

                switch (operator) {
                    case "+": result = result.add(next); break;
                    case "-": result = result.subtract(next); break;
                    case "*": result = result.multiply(next); break;
                    case "/": result = result.divide(next, 2, RoundingMode.HALF_UP); break;
                }
            }
            return result;
        } catch (Exception e) {
            log.error("计算简单表达式失败: {}", expression, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 工具方法：获取随机类型
     */
    private String getRandomType(List<String> types) {
        if (types == null || types.isEmpty()) {
            // 默认包含所有类型
            String[] allTypes = {"AddAndSub", "MulAndDiv", "Mixed"};
            return allTypes[random.nextInt(allTypes.length)];
        }
        return types.get(random.nextInt(types.size()));
    }

    /**
     * 工具方法：获取随机难度
     */
    private String getRandomDifficulty(List<String> difficulties) {
        if (difficulties == null || difficulties.isEmpty()) {
            // 默认包含所有难度
            String[] allDifficulties = {"easy", "medium", "hard"};
            return allDifficulties[random.nextInt(allDifficulties.length)];
        }
        return difficulties.get(random.nextInt(difficulties.size()));
    }

    /**
     * 工具方法：获取随机数
     */
    private int getRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 工具方法：寻找能整除的除数
     */
    private int findDivisor(int number) {
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return i;
            }
        }
        return 1; // 如果找不到，返回1
    }
}