/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.rdb;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import kiss.I;
import kiss.Model;
import kiss.Property;
import kiss.Variable;
import kiss.WiseTriConsumer;
import kiss.Ⅲ;
import reincarnation.Reincarnation;
import reincarnation.coder.Code;
import reincarnation.coder.Coder;
import reincarnation.coder.DelegatableCoder;
import reincarnation.operator.AccessMode;
import reincarnation.operator.AssignOperator;
import reincarnation.operator.BinaryOperator;
import reincarnation.operator.UnaryOperator;
import typewriter.api.Identifiable;
import typewriter.api.QueryDSL;
import typewriter.api.Specifier;
import typewriter.query.SQLCodingOption;

public class SQLCoder extends Coder<SQLCodingOption> {

    private final Method method;

    private final SerializedLambda lambda;

    private final Dialect dialect;

    private final SQLBuilder sql = new SQLBuilder();

    public SQLCoder(Method method, SerializedLambda lambda, Dialect dialect) {
        this.method = method;
        this.lambda = lambda;
        this.dialect = dialect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Reincarnation reincarnation) {
        reincarnation.methods.forEach(this::writeMethod);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writePackage(Package info) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeType(Class type, Runnable inner) {
        unsupportedSyntax("type");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeField(Field field) {
        unsupportedSyntax("field");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeStaticField(Field field) {
        unsupportedSyntax("static field");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeInitializer(Code code) {
        unsupportedSyntax("initializer");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeStaticInitializer(Code code) {
        unsupportedSyntax("static initializer");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeConstructor(Constructor constructor, Code<Code> code) {
        unsupportedSyntax("constructor");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeMethod(Method method, Code<Code> code) {
        if (method.equals(this.method)) {
            code.write(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeLambda(Method method, List<Code> contexts, Code code) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeStatement(Code<?> code) {
        code.write(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeReturn(Variable<Code> code) {
        code.to(c -> c.write(this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeYield(Code code) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeBoolean(boolean code) {
        writeValue(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeChar(char code) {
        writeValue(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeInt(int code) {
        writeValue(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeLong(long code) {
        writeValue(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFloat(float code) {
        writeValue(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeDouble(double code) {
        writeValue(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeString(String code) {
        writeValue(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeStringConcatenation(Iterator<Code> code) {
        unsupportedSyntax("string concatenation");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTextBlock(List<String> code) {
        unsupportedSyntax("text block");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeClass(Class code) {
        unsupportedSyntax("class literal");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeThis() {
        unsupportedSyntax("this");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeNull() {
        write("NULL");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeEnclose(Runnable code) {
        write("(");
        code.run();
        write(")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeAssignOperation(Code left, AssignOperator operator, Code right) {
        write(left, space, operator, space, right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeBinaryOperation(Code left, BinaryOperator operator, Code right) {
        String op = switch (operator) {
        case EQUAL -> "=";
        case NOT_EQUALS -> "!=";
        case AND -> "AND";
        case OR -> "OR";
        case XOR -> "XOR";
        default -> operator.toString();
        };

        write(left, space, op, space, right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writePositiveOperation(Code code) {
        write(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeNegativeOperation(Code code) {
        write("!", code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeUnaryOperation(Code code, UnaryOperator operator) {
        unsupportedSyntax("unary operation");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeInstanceof(Code code, Class type, Code cast) {
        unsupportedSyntax("instance of");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeLocalVariable(Type type, int index, String name) {
        if (index == 0 && type instanceof Class clazz && Identifiable.class.isAssignableFrom(clazz)) {
            // dummy model
            System.out.println("DUMMY");
        } else if (index < lambda.getCapturedArgCount()) {
            writeValue(lambda.getCapturedArg(index));
        } else {
            System.out.println(index + " " + type);
            write(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeAccessField(Field field, Code context, AccessMode mode) {
        switch (mode) {
        case THIS:
            write(field.getName());
            break;

        case CAST:
            write(" ", field.getName(), " ");
            break;

        default:
            unsupportedSyntax("field access " + mode + " mode");
            break;
        }
    }

    private static final Set<Class> IgnorableTypes = Set
            .of(QueryDSL.class, Integer.class, Long.class, Float.class, Double.class, Byte.class, Short.class, Boolean.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeAccessType(Class type) {
        if (IgnorableTypes.contains(type)) {
            // accept and ignore
        } else {
            unsupportedSyntax(type + " is not permitted by accessing type.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeAccessArray(Code array, Code index) {
        unsupportedSyntax("read array");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeAccessArrayLength(Code array) {
        unsupportedSyntax("array length");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeConstructorCall(Constructor constructor, List<Code> params) {
        unsupportedSyntax("constructor call");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeSuperConstructorCall(Constructor constructor, List<Code> params) {
        unsupportedSyntax("super constructor call");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeThisConstructorCall(Constructor constructor, List<Code> params) {
        unsupportedSyntax("this constuctor call");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeMethodCall(Method method, Code context, List<Code> params, AccessMode mode) {
        Class<?> clazz = method.getDeclaringClass();
        if (Identifiable.class.isAssignableFrom(clazz)) {
            Model model = Model.of(clazz);
            Property property = model.property(Specifier.inspectPropertyName(method));
            if (property != null) {
                write(property.name);
            }
        } else if (QueryDSL.class.isAssignableFrom(clazz)) {
            write(command(method.getName()), " ");
            for (Code param : params) {
                param.write(this);
            }
            write(" ");
        } else {
            WiseTriConsumer<SQLCoder, Code, List<Code>> translator = searchTranslator(method.getDeclaringClass(), method.getName(), method
                    .getParameterTypes());

            if (translator != null) {
                translator.accept(this, context, params);
            } else {
                context.write(this);
                params.forEach(p -> p.write(this));
            }
        }
    }

    private static String command(String command) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 0) builder.append(" ");
                builder.append(c);
            } else {
                builder.append(Character.toUpperCase(c));
            }
        }
        return builder.toString();
    }

    private WiseTriConsumer<SQLCoder, Code, List<Code>> searchTranslator(Class base, String name, Class[] paramTypes) {
        for (Class type : Model.collectTypes(base)) {
            WiseTriConsumer<SQLCoder, Code, List<Code>> translator = methods.get(I.pair(type, name, paramTypes));
            if (translator != null) {
                return translator;
            }
        }
        return null;
    }

    private static final Map<Ⅲ<Class, String, Class[]>, WiseTriConsumer<SQLCoder, Code, List<Code>>> methods = new HashMap();

    private static void registerTranslator(Class owner, String methodName, Class[] paramTypes, WiseTriConsumer<SQLCoder, Code, List<Code>> translator) {
        methods.put(I.pair(owner, methodName, paramTypes), translator);
    }

    static {
        registerTranslator(Object.class, "equals", new Class[] {Object.class}, (coder, context, params) -> {
            coder.write(context, " = ");
            params.forEach(coder::write);
        });

        registerTranslator(String.class, "contains", new Class[] {CharSequence.class}, (coder, context, params) -> {
            coder.write(context, " LIKE '%", noquote(params), "%'");
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeConstructorReference(Constructor constructor) {
        unsupportedSyntax("constructor ref");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeMethodReference(Method method, Code context) {
        unsupportedSyntax("method ref");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeStaticMethodReference(Method method) {
        unsupportedSyntax("static method ref");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeCreateArray(Class type, List<Code> dimensions) {
        unsupportedSyntax("create array");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeCreateArray(Class type, List<Code> dimensions, List<Code> initialValues) {
        for (Code value : initialValues) {
            value.write(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTernary(Code condition, Code then, Code elze) {
        unsupportedSyntax("ternary");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeCast(Class type, Code code) {
        unsupportedSyntax("cast");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeLineComment(Object comment) {
        unsupportedSyntax("line comment");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeAssert(Code code, Variable<Code> message) {
        unsupportedSyntax("assert");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeThrow(Code code) {
        unsupportedSyntax("throw");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeBreak(Optional<String> label, boolean omitLabel) {
        unsupportedSyntax("break");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeContinue(Optional<String> label, boolean omitLabel) {
        unsupportedSyntax("continue");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeIf(Code condition, Code then, Code elze, Code follow) {
        unsupportedSyntax("if");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFor(Optional<String> label, Code initialize, Code condition, Code updater, Runnable inner, Code follow) {
        unsupportedSyntax("for");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeIterableFor(Optional<String> label, Code variable, Code iterable, Runnable inner, Code follow) {
        unsupportedSyntax("iterable for");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeWhile(Optional<String> label, Code condition, Runnable inner, Code follow) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeDoWhile(Optional<String> label, Code condition, Runnable inner, Code follow) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeInfinitLoop(Optional<String> label, Runnable inner, Code follow) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTryCatchFinally(Code tryBlock, List<Ⅲ<Class, String, Code>> catchBlocks, Code follow) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeSwitch(boolean statement, Optional<String> label, Code condition, Class type, Runnable caseProcess, Code follow) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeIntCase(boolean statement, List<Integer> values, Code caseBlock) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeCharCase(boolean statement, List<Character> values, Code caseBlock) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <E extends Enum> void writeEnumCase(boolean statement, Class<E> type, List<E> values, Code caseBlock) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeStringCase(boolean statement, List<String> values, Code caseBlock) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeDefaultCase(boolean statement, Code defaultBlock) {
    }

    /**
     * Write some value.
     * 
     * @param value
     */
    private void writeValue(Object value) {
        if (value instanceof RDB rdb) {
            write(rdb.tableName);
        } else if (value instanceof String text) {
            write("'", text, "'");
        } else {
            write(value);
        }
    }

    private void unsupportedSyntax(String keyword) {
        throw new UnsupportedOperationException(keyword + " is not supported in query wirter.");
    }

    /**
     * Delegate to NoQuotableCoder.
     * 
     * @param codes
     * @return
     */
    private static Code noquote(Code code) {
        return noquote(List.of(code));
    }

    /**
     * Delegate to NoQuotableCoder.
     * 
     * @param codes
     * @return
     */
    private static Code noquote(List<Code> codes) {
        return original -> {
            Coder coder = new DelegatableCoder(original) {

                @Override
                public void writeString(String code) {
                    write(code);
                }
            };
            coder.write(codes.toArray());
        };
    }
}
