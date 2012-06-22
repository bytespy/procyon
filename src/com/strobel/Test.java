package com.strobel;

import com.strobel.expressions.LambdaExpression;
import com.strobel.expressions.ParameterExpression;
import com.strobel.reflection.BindingFlags;
import com.strobel.reflection.MethodInfo;
import com.strobel.reflection.PrimitiveTypes;
import com.strobel.reflection.Type;
import com.strobel.reflection.Types;
import com.sun.source.tree.TreeVisitor;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;

import static com.strobel.expressions.Expression.*;

/**
 * @author Mike Strobel
 */
@SuppressWarnings("UnusedDeclaration")
public class Test {
    public static void main(final String[] args) {
        compilerToolsTest();
        primitiveTest();
        expressionTest();
        genericMethodTest();
        arrayTypeTest();
    }

    private static void compilerToolsTest() {
        final Context context = new Context();
        final Type<?> mapType = Type.of(HashMap.class);
        final Type<?> genericMapType = mapType.makeGenericType(Types.String, Types.Date);

        final Symbol symbol = JavaCompiler.instance(context).resolveIdent(ITest2.class.getName());

        final Class<? extends String> aClass = "".getClass();

        System.out.println(mapType);
        System.out.println(genericMapType);
        System.out.println(Type.of(ITest2.class));
        System.out.println(Arrays.toString(genericMapType.getMembers(BindingFlags.AllDeclared).toArray()));
        System.out.println(Arrays.toString(genericMapType.getMembers(BindingFlags.All).toArray()));
        System.out.println(Arrays.toString(Type.of(Type.class).getMethods(BindingFlags.AllStatic).toArray()));

        System.out.println(
            Type.of(Type.class)
                .makeGenericType(Types.BigInteger)
                .getMethods(
                    EnumSet.of(
                        BindingFlags.Public,
                        BindingFlags.NonPublic,
                        BindingFlags.Static,
                        BindingFlags.FlattenHierarchy
                    )
                )
                .get(3)
                .getErasedSignature()
        );

        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getConstructors().get(3).getSignature());
        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getConstructors().get(3).getErasedSignature());
        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getMethods().get(4).getSignature());
        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getMethods().get(4).getErasedSignature());
        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getConstructors().get(3).getDescription());
        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getConstructors().get(3).getSimpleDescription());
        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getConstructors().get(3).getErasedDescription());
        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getMethods().get(4).getDescription());
        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getMethods().get(4).getSimpleDescription());
        System.out.println(Type.of(HashMap.class).makeGenericType(Types.String, Types.Date).getMethods().get(4).getErasedDescription());
        System.out.println(Arrays.toString(genericMapType.getNestedType("java.util.HashMap$ValueIterator", BindingFlags.All).getMethods().toArray()));
//        System.out.println(genericMapType.getMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic).get(30));
    }

    private static void primitiveTest() {
        System.out.println(PrimitiveTypes.Integer);
        System.out.println(Types.Integer);
        System.out.println(PrimitiveTypes.Integer == Types.Integer);
        System.out.println(PrimitiveTypes.Integer.getErasedClass() == Types.Integer.getErasedClass());
    }

    private static void genericMethodTest() {
        final Type map = Type.of(HashMap.class).makeGenericType(Types.String, Types.Date);
        final MethodInfo putMethod = map.getMethod("put");

        System.out.println("Full description: " + map.getFullDescription());
        System.out.println("Type signature: " + map.getSignature());
        System.out.println("Erased type signature: " + map.getErasedSignature());
        System.out.println("Method signature: " + putMethod.getSignature());
        System.out.println("Erased method signature: " + putMethod.getErasedSignature());
    }

    private static void arrayTypeTest() {
        final Type<String[]> stringArray = Types.String.makeArrayType();

        System.out.println("Full description: " + stringArray.getFullDescription());
        System.out.println("Type signature: " + stringArray.getSignature());
        System.out.println("Erased type signature: " + stringArray.getSignature());
        System.out.println("Element type signature: " + stringArray.getElementType().getSignature());
    }

    private static void expressionTest() {
        final ParameterExpression number = parameter(PrimitiveTypes.Integer, "number");

        final LambdaExpression<ITest> lambda = lambda(
            Type.of(ITest.class),
            condition(
                equal(number, constant(0)),
                constant("zero"),
                condition(
                    lessThan(number, constant(0)),
                    constant("negative"),
                    constant("positive")
                )
            ),
            number
        );

        System.out.println(lambda);
    }

    private static class NullTree extends JCTree {
        @Override
        public Tag getTag() {
            return Tag.NO_TAG;
        }

        @Override
        public void accept(final Visitor v) {
        }

        @Override
        public <R, D> R accept(final TreeVisitor<R, D> v, final D d) {
            return null;
        }

        @Override
        public Kind getKind() {
            return Kind.NULL_LITERAL;
        }
    }
}

interface ITest {
    String testNumber(final int number);
}

interface ITest2<T extends String & Comparable<String> & Serializable, T2 extends T> {
    T2 test(final T t);
    <X, Y> Y doSomething(X o);
}