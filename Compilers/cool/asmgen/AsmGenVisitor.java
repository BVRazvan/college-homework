package cool.asmgen;

import cool.compiler.Compiler;
import cool.parser.*;
import cool.structures.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.Locale.filter;
import static java.util.Locale.of;

public class AsmGenVisitor implements ASTVisitor<ST> {
    private static STGroupFile templates = new STGroupFile("cool/asmgen/asmgen.stg");  // Template file for code generation
    private final Map<String, String> inheritanceChain;
    private final Map<String, List<String>> classFunctions;
    private final Map<String, List<String>> classAttrs;
    private final Map<String, List<String>> classAttrsNames;
    private final Map<String, Scope> scopeMap;
    private Map<String, Integer> stringMap = new HashMap<>();
    private Map<Integer, Integer> integerMap = new HashMap<>();
    private Map<String, Integer> boolMap = new HashMap<>();
    private Map<String, List<Integer>> classHierarchy = new HashMap<>();
    private int stringCounter;
    private int intCounter;
    private int classCounter;
    private int dispatchCounter;
    private String intTag = "2";
    private String strTag = "3";
    private String boolTag = "4";

    // Main sections of the generated assembly
    // objProt, methodTable, initRoutines, textMain
    private ST objProt;    // Contains classes' object prototype definiton (.data section)
    private ST methodTable;    // Contains classes' table of methods (.data section)
    private ST initRoutines;    // Contains classes' initialization methods (in .text section)
    private ST functionsDef; // Contains functions' code
    private ST intLiterals; // Contains literal values
    private ST stringLiterals; // Contains literal values
    private ST boolLiterals; // Contains literal values
    private ST classNames; // Contains class names
    private ST classObjProts;
    private int boolCounter;
    private int univCounter;

    public AsmGenVisitor(Map<String, String> inheritanceChain, Map<String, List<String>> classFunctions, Map<String, List<String>> classAttrs, Map<String, List<String>> classAttrsNames, Map<String, Scope> scopeMap) {
        this.inheritanceChain = inheritanceChain;
        this.classFunctions = classFunctions;
        this.classAttrs = classAttrs;
        this.classAttrsNames = classAttrsNames;
        this.scopeMap = scopeMap;
    }

    @Override
    public ST visit(ClassDef classDef) {
        addStringLiteral(classDef.getType().getToken().getText());
        classNames.add("e", templates.getInstanceOf("class_const").add("id", "str_const" + stringMap.get(classDef.getType().getToken().getText())));
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", classDef.getType().getToken().getText() + "_protObj"));
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", classDef.getType().getToken().getText() + "_init"));

        ST template = templates.getInstanceOf("class_meth").add("id", classDef.getType().getToken().getText());
        List<String> listFunctions = new ArrayList<>();
        populateDispTable(listFunctions, classDef.getType().getToken().getText());
        template.add("vals", listFunctions);

        methodTable.add("e", template);

        ST classObj = templates.getInstanceOf("class_obj");
        List<ST> exprs = new ArrayList<>();

        var className = classDef.getType().getToken().getText();
        classHierarchy.put(className, new ArrayList<>(Arrays.asList(classCounter, classCounter)));
        while (inheritanceChain.containsKey(className)) {
            className = inheritanceChain.get(className);
            var list = classHierarchy.get(className);
            list.set(1, classCounter);
        }

        for (var feature : classDef.getFeatures()) {
            if (feature instanceof VariableDef) {
                exprs.add(feature.accept(this));
            } else {
                feature.accept(this);
            }
        }

        List<String> listAttrs = new ArrayList<>();
        populateAttrTable(listAttrs, classDef.getType().getToken().getText());

        List<String> classKey = new ArrayList<>(Collections.nCopies(listAttrs.size() + 3, ".word"));
        List<String> classValues = new ArrayList<>(Arrays.asList(String.valueOf(classCounter++), String.valueOf(listAttrs.size() + 3), classDef.getType().getToken().getText() + "_dispTab"));

        for (var attr : listAttrs) {
            if (isNumeric(attr)) {
                addStringLiteral(attr);
                classValues.add("int_const" + integerMap.get(Integer.parseInt(attr)));
            } else if (attr.equals("true") || attr.equals("false")) {
                classValues.add("bool_const" + (attr.equals(true) ? "1" : "0"));
            } else if (attr.equals("null")) {
                classValues.add("0");
            } else {
                classValues.add("str_const" + stringMap.get(attr));
            }
        }

        List<ST> sts = new ArrayList<>();
        for (var expr : exprs) {
            var attr = (ST) expr.getAttribute("e");
            sts.add(templates.getInstanceOf("literal")
                .add("id", attr.getAttribute("id").toString()));
        }
        initRoutines
                .add("e", templates.getInstanceOf("class_init")
                .add("id", classDef.getType().getToken().getText())
                .add("parent", inheritanceChain.get(classDef.getType().getToken().getText()) == null ? "Object" : inheritanceChain.get(classDef.getType().getToken().getText()))
                        .add("e", exprs));

        classObj.add("id", classDef.getType().getToken().getText()).add("keys", classKey).add("vals", classValues);
        objProt.add("e", classObj);

        return null;
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private void populateAttrTable(List<String> listAttrs, String className) {
        if (inheritanceChain.containsKey(className)) {
            populateAttrTable(listAttrs, inheritanceChain.get(className));
        }
        var list = classAttrs.get(className);
        if (list != null) {
            listAttrs.addAll(list);
        }
    }

    private void populateAttrNamesTable(List<String> listAttrsNames, String className) {
        if (inheritanceChain.containsKey(className)) {
            populateAttrNamesTable(listAttrsNames, inheritanceChain.get(className));
        }
        var list = classAttrsNames.get(className);
        if (list != null) {
            listAttrsNames.addAll(list);
        }
    }


    private void populateDispTable(List<String> listFunctions, String className) {
        if (inheritanceChain.containsKey(className)) {
            populateDispTable(listFunctions, inheritanceChain.get(className));
        }
        var list = classFunctions.get(className);
        if (list != null) {
            for (var newFunc : list) {
                var modFunc = newFunc.substring(newFunc.indexOf('.') + 1);
                boolean ok = false;
                for (var func : listFunctions) {
                    var actFunc = func.substring(func.indexOf('.') + 1);
                    if (modFunc.equals(actFunc)) {
                        listFunctions.set(listFunctions.indexOf(func), newFunc);
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    listFunctions.add(newFunc);
                }
            }
        }
    }

    @Override
    public ST visit(Program program) {
        objProt = templates.getInstanceOf("sequence");
        methodTable = templates.getInstanceOf("sequence");
        initRoutines = templates.getInstanceOf("sequence");
        functionsDef = templates.getInstanceOf("sequence");
        intLiterals = templates.getInstanceOf("sequence");
        stringLiterals = templates.getInstanceOf("sequence");
        boolLiterals = templates.getInstanceOf("sequence");
        classNames = templates.getInstanceOf("sequence"); // Contains class names
        classObjProts = templates.getInstanceOf("sequence");

        hardCoding(program.parserContext);

        for (ASTNode e : program.getClassDefs()) {
            e.accept(this);
        }

        //assembly-ing it all together. HA! get it?
        var programST = templates.getInstanceOf("program");
        programST.add("objProt", objProt);
        programST.add("methodTable", methodTable);
        programST.add("initRoutines", initRoutines);
        programST.add("functionsDef", functionsDef);
        programST.add("intLiterals", intLiterals);
        programST.add("stringLiterals", stringLiterals);
        programST.add("boolLiterals", boolLiterals);
        programST.add("classNames", classNames);
        programST.add("classObjProts", classObjProts);

        return programST;
    }

    private void hardCoding(ParserRuleContext ctx) {
        addStringLiteral(Compiler.fileName);
        addStringLiteral("");
        addStringLiteral("Object");
        classNames.add("e", templates.getInstanceOf("class_const").add("id", "str_const" + String.valueOf(stringCounter - 1)));
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "Object_protObj"));
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "Object_init"));
        List<String> objectKey = new ArrayList<>(Collections.nCopies(3, ".word"));
        List<String> objectValues = new ArrayList<>(Arrays.asList(String.valueOf(classCounter++), "3", "Object_dispTab"));
        objProt.add("e", templates.getInstanceOf("class_obj").add("id", "Object").add("keys", objectKey).add("vals", objectValues));
        List<String> objectMeths = new ArrayList<>(Arrays.asList("Object.abort", "Object.type_name", "Object.copy"));
        methodTable.add("e", templates.getInstanceOf("class_meth").add("id", "Object").add("vals", objectMeths));
        classFunctions.put("Object", new ArrayList<String>(Arrays.asList("Object.abort", "Object.type_name", "Object.copy")));
        classHierarchy.put("Object", new ArrayList<>(Arrays.asList(0, 4)));

        addStringLiteral("IO");
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "IO_protObj"));
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "IO_init"));
        classNames.add("e", templates.getInstanceOf("class_const").add("id", "str_const" + String.valueOf(stringCounter - 1)));
        List<String> ioKey = new ArrayList<>(Collections.nCopies(3, ".word"));
        List<String> ioValues = new ArrayList<>(Arrays.asList(String.valueOf(classCounter++), "3", "IO_dispTab"));
        objProt.add("e", templates.getInstanceOf("class_obj").add("id", "IO").add("keys", ioKey).add("vals", ioValues));
        List<String> ioMeths = new ArrayList<>(Arrays.asList("Object.abort", "Object.type_name", "Object.copy", "IO.out_string", "IO.out_int", "IO.in_string", "IO.in_int"));
        methodTable.add("e", templates.getInstanceOf("class_meth").add("id", "IO").add("vals", ioMeths));
        classFunctions.put("IO", new ArrayList<String>(Arrays.asList("IO.out_string", "IO.out_int", "IO.in_string", "IO.in_int")));
        initRoutines.add("e", templates.getInstanceOf("class_init").add("id", "IO").add("parent", "Object"));
        classHierarchy.put("IO", new ArrayList<>(Arrays.asList(1, 1)));

        addStringLiteral("Int");
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "Int_protObj"));
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "Int_init"));
        classNames.add("e", templates.getInstanceOf("class_const").add("id", "str_const" + String.valueOf(stringCounter - 1)));
        List<String> intKey = new ArrayList<>(Arrays.asList(".word", ".word", ".word", ".word"));
        List<String> intValues = new ArrayList<>(Arrays.asList(String.valueOf(classCounter++), "4", "Int_dispTab", "0"));
        objProt.add("e", templates.getInstanceOf("class_obj").add("id", "Int").add("keys", intKey).add("vals", intValues));
        List<String> intMeths = new ArrayList<>(Arrays.asList("Object.abort", "Object.type_name", "Object.copy"));
        methodTable.add("e", templates.getInstanceOf("class_meth").add("id", "Int").add("vals", intMeths));
        classFunctions.put("Int", new ArrayList<String>());
        initRoutines.add("e", templates.getInstanceOf("class_init").add("id", "Int").add("parent", "Object"));
        classHierarchy.put("Int", new ArrayList<>(Arrays.asList(2, 2)));

        addStringLiteral("String");
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "String_protObj"));
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "String_init"));
        classNames.add("e", templates.getInstanceOf("class_const").add("id", "str_const" + String.valueOf(stringCounter - 1)));
        addIntLiteral(0);
        List<String> stringKey = new ArrayList<>(Arrays.asList(".word", ".word", ".word", ".word", ".asciiz", ".align"));
        List<String> stringValues = new ArrayList<>(Arrays.asList(String.valueOf(classCounter++), "5", "String_dispTab", "int_const" + String.valueOf(intCounter - 1), "\"\"", "2"));
        objProt.add("e", templates.getInstanceOf("class_obj").add("id", "String").add("keys", stringKey).add("vals", stringValues));
        List<String> stringMeths = new ArrayList<>(Arrays.asList("Object.abort", "Object.type_name", "Object.copy", "String.length", "String.concat", "String.substr"));
        methodTable.add("e", templates.getInstanceOf("class_meth").add("id", "String").add("vals", stringMeths));
        classFunctions.put("String", new ArrayList<String>(Arrays.asList("String.length", "String.concat", "String.substr")));
        initRoutines.add("e", templates.getInstanceOf("class_init").add("id", "String").add("parent", "Object"));
        classHierarchy.put("String", new ArrayList<>(Arrays.asList(3, 3)));

        addStringLiteral("Bool");
        addBoolLiteral(0);
        addBoolLiteral(1);
        boolMap.put("false", 0);
        boolMap.put("true", 1);

        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "Bool_protObj"));
        classObjProts.add("e", templates.getInstanceOf("class_const").add("id", "Bool_init"));
        classNames.add("e", templates.getInstanceOf("class_const").add("id", "str_const" + String.valueOf(stringCounter - 1)));
        List<String> boolKey = new ArrayList<>(Arrays.asList(".word", ".word", ".word", ".word"));
        List<String> boolValues = new ArrayList<>(Arrays.asList(String.valueOf(classCounter++), "4", "Bool_dispTab", "0"));
        objProt.add("e", templates.getInstanceOf("class_obj").add("id", "Bool").add("keys", boolKey).add("vals", boolValues));
        List<String> boolMeths = new ArrayList<>(Arrays.asList("Object.abort", "Object.type_name", "Object.copy"));
        methodTable.add("e", templates.getInstanceOf("class_meth").add("id", "Bool").add("vals", boolMeths));
        classFunctions.put("Bool", new ArrayList<String>());
        initRoutines.add("e", templates.getInstanceOf("class_init").add("id", "Bool").add("parent", "Object"));
        classHierarchy.put("Bool", new ArrayList<>(Arrays.asList(4, 4)));
    }

    @Override
    public ST visit(Type type) {
        if (type.getToken().getText().equals("Int")) {
            return templates.getInstanceOf("literal")
                    .add("id", "int_const" + integerMap.get(0));
        } else if (type.getToken().getText().equals("Bool")) {
            return templates.getInstanceOf("literal")
                    .add("id", "bool_const0");
        } else if (type.getToken().getText().equals("String")) {
            return templates.getInstanceOf("literal")
                    .add("id", "str_const" + stringMap.get(""));
        } else if (!type.getToken().getText().equals("SELF_TYPE")) {
            return templates.getInstanceOf("move_null");
        } else {
            return null;
        }
    }

    @Override
    public ST visit(Id id) {
        if (id.getToken().getText().equals("self")) {
            return templates.getInstanceOf("self");
        }
        var scope = id.getScope();
        if (scope instanceof CaseScope) {
            return templates.getInstanceOf("attribute")
                    .add("offset", -4)
                    .add("reg", "fp");
        }

        if (!(scope instanceof ClassSymbol)) {
            scope = scope.getParent();
        }
        var formal = scope.lookup(id.getToken().getText() + "_attribute");
        if (formal != null) {
            if (formal.isFormal()) {
                return templates.getInstanceOf("formal")
                        .add("offset", (((FieldSymbol) formal).getFieldIndex() + 3) * 4)
                        .add("reg", "fp");
            } else if (formal.isStackAdded()) {
                return templates.getInstanceOf("formal")
                        .add("offset", -((((FieldSymbol) formal).getFieldIndex()) + 1) * 4)
                        .add("reg", "fp");
            }
        }
        while (!(scope instanceof ClassSymbol)) {
            scope = scope.getParent();
        }
        List<String> listAttrs = new ArrayList<>();
        populateAttrNamesTable(listAttrs, ((ClassSymbol) scope).getName());
        var offset = getOffsetAttr(listAttrs, id);
        if (offset == -1) {
            List<String> listFunctions = new ArrayList<>();
            populateDispTable(listFunctions, ((ClassSymbol) scope).getName());
            offset = findOffsetFunc(listFunctions, id);
        }
        return templates.getInstanceOf("attribute")
                .add("offset", offset)
                .add("reg", "s0");
    }

    @Override
    public ST visit(VariableDef variableDef) {
        var idRet = variableDef.getId().accept(this);
        if (variableDef.getExpression() == null) {
            var type = variableDef.getType().getToken().getText();
            if (type.equals("Int")) {
                return templates.getInstanceOf("assign")
                        .add("e", templates.getInstanceOf("literal")
                                .add("id", "int_const" + integerMap.get(0)))
                        .add("reg", "s0")
                        .add("offset", idRet.getAttribute("offset"));
            } else if (type.equals("Bool")) {
                return templates.getInstanceOf("assign")
                        .add("e", templates.getInstanceOf("literal")
                                .add("id", "bool_const0"))
                        .add("reg", "s0")
                        .add("offset", idRet.getAttribute("offset"));
            } else if (type.equals("String")) {
                return templates.getInstanceOf("assign")
                        .add("e", templates.getInstanceOf("literal")
                                .add("id", "str_const" + stringMap.get("")))
                        .add("reg", "s0")
                        .add("offset", idRet.getAttribute("offset"));
            } else {
                return templates.getInstanceOf("assign")
                        .add("e", templates.getInstanceOf("literal")
                                .add("id", 0))
                        .add("reg", "s0")
                        .add("offset", idRet.getAttribute("offset"));
            }
        }
        var exprRet = variableDef.getExpression().accept(this);
        return templates.getInstanceOf("assign")
                .add("e", exprRet)
                .add("reg", "s0")
                .add("offset", idRet.getAttribute("offset"));
    }

    @Override
    public ST visit(FunctionDef functionDef) {
        functionDef.getFunctionId().accept(this);
        functionDef.getType().accept(this);
        for (var formal : functionDef.getFormals()) {
            formal.accept(this);
        }

        var functionTemplate = templates.getInstanceOf("function_text");
        functionTemplate.add("classId", ((ClassSymbol)functionDef.getScope()).getName());
        functionTemplate.add("methodId", functionDef.getFunctionId().getToken().getText());
        functionTemplate.add("text", functionDef.getBody().accept(this));
        functionTemplate.add("space_params", functionDef.getFormals().size() * 4);
        functionsDef.add("e", functionTemplate);
        return null;
    }

    @Override
    public ST visit(Formal formal) {
        var id = formal.getId();
        return id.accept(this);
    }

    @Override
    public ST visit(IntLiteral intLiteral) {
        var intLit = addIntLiteral(Integer.parseInt(intLiteral.getToken().getText()));
        return templates.getInstanceOf("literal").add("id", "int_const" + integerMap.get(Integer.parseInt(intLiteral.getToken().getText())));
    }

    @Override
    public ST visit(BoolLiteral boolLiteral) {
        return templates.getInstanceOf("literal")
                .add("id", "bool_const" + (boolLiteral.getToken().getText().equals("true") ? "1" : "0"));
    }

    @Override
    public ST visit(StringLiteral stringLiteral) {
        var stringLit = addStringLiteral(stringLiteral.getToken().getText());
        return templates.getInstanceOf("literal").add("id", "str_const" + stringMap.get(stringLiteral.getToken().getText()));
    }

    private ST addStringLiteral(String stringLiteral) {
        boolean toAdd = false;
        if (!stringMap.containsKey(stringLiteral)) {
            stringMap.put(stringLiteral, stringCounter++);
            toAdd = true;
        }

        int len = stringLiteral.length() + 1;
        int words = 4 + len / 4 + ((len % 4 > 0) ? 1 : 0);
        addIntLiteral(words);
        addIntLiteral(len - 1);

        String intConst = "int_const" + integerMap.get(len - 1);
        List<String> values = new ArrayList<>();
        values.add(strTag);
        values.add(String.valueOf(words));
        values.add("String_dispTab");
        values.add(intConst);

        var stringLit =  templates.getInstanceOf("string_const")
                .add("id", stringMap.get(stringLiteral))
                .add("vals", values)
                .add("str", stringLiteral);

        if (toAdd) {
            stringLiterals.add("e", stringLit);
        }
        return stringLit;
    }

    private ST addIntLiteral(int words) {
        boolean toAdd = false;
        if (!integerMap.containsKey(words)) {
            integerMap.put(words, intCounter++);
            toAdd = true;
        }

        List<String> values = new ArrayList<>();
        values.add(intTag);
        values.add("4");
        values.add("Int_dispTab");
        values.add(String.valueOf(words));

        var intLiteral = templates.getInstanceOf("int_const")
                .add("id", integerMap.get(words))
                .add("vals", values);
        if (toAdd) {
            intLiterals.add("e", intLiteral);
        }
        return intLiteral;
    }

    private void addBoolLiteral(int words) {
        List<String> values = new ArrayList<>();
        values.add(boolTag);
        values.add("4");
        values.add("Bool_dispTab");
        values.add(String.valueOf(words));

        var boolLiteral = templates.getInstanceOf("bool_const")
                .add("id", boolCounter++)
                .add("vals", values);
        boolLiterals.add("e", boolLiteral);
    }

    @Override
    public ST visit(Relational relational) {
        var left = relational.getLeft().accept(this);
        var right = relational.getRight().accept(this);
        var sign = new String();
        switch (relational.getToken().getText()) {
            case "=":
                sign = "beq";
                break;
            case "<":
                sign = "blt";
                break;
            case "<=":
                sign = "ble";

        }
        if (sign.equals("beq")) {
            return templates.getInstanceOf("equals")
                    .add("e1", left)
                    .add("e2", right)
                    .add("eq_counter", univCounter++)
                    .add("op", sign);
        } else {
            return templates.getInstanceOf("relational")
                    .add("e1", left)
                    .add("e2", right)
                    .add("compare_counter", univCounter++)
                    .add("op", sign);
        }
    }

    @Override
    public ST visit(Arithmetic arithmetic) {
        var left = arithmetic.getLeft().accept(this);
        var right = arithmetic.getRight().accept(this);

        String sign = new String();
        switch (arithmetic.getToken().getText()) {
            case "+":
                sign = "add";
                break;
            case "-":
                sign = "sub";
                break;
            case "*":
                sign = "mul";
                break;
            case "/":
                sign = "div";
                break;
            default:
                break;
        }
        return templates.getInstanceOf("arithmetic")
                .add("op", sign)
                .add("e1", left)
                .add("e2", right);
    }

    @Override
    public ST visit(Paren paren) {
        return templates.getInstanceOf("sequence")
                .add("e", paren.getExpression().accept(this));
    }

    @Override
    public ST visit(Complement complement) {
        var exprST = complement.getExpression().accept(this);
        return templates.getInstanceOf("neg").add("e", exprST);
    }

    @Override
    public ST visit(Not not) {
        return templates.getInstanceOf("not")
                .add("e", not.getExpression().accept(this))
                .add("not_counter", univCounter++);
    }

    @Override
    public ST visit(Assign assign) {
        var scope = assign.getId().getScope();
        while (!(scope instanceof ClassSymbol)) {
            scope = scope.getParent();
        }
        var e = assign.getExpression().accept(this);

        List<String> listAttrsNames = new ArrayList<>();
        populateAttrNamesTable(listAttrsNames, ((ClassSymbol) scope).getName());
        List<String> listAttrs = new ArrayList<>();
        var ret = assign.getId().accept(this);
        var offset = ret.getAttribute("offset");
        var reg = ret.getAttribute("reg");

        return templates.getInstanceOf("assign")
                .add("e", e)
                .add("offset", offset)
                .add("reg", reg);
    }

    @Override
    public ST visit(IsVoid isVoid) {
        return templates.getInstanceOf("isvoid")
                .add("e", isVoid.getExpression().accept(this))
                .add("isvoid_counter", univCounter++);
    }

    @Override
    public ST visit(New aNew) {
        var type = aNew.getType();
        if (type.getToken().getText().equals("SELF_TYPE")) {
            return templates.getInstanceOf("self_type");
        }
        return templates.getInstanceOf("new")
                .add("id", aNew.getType().getToken().getText());
    }

    @Override
    public ST visit(Call call) {
        var id = call.getId();
        var params = call.getParameters();
        var scope = id.getScope();
        var initScope = id.getScope();
        List<String> listFunctions = new ArrayList<>();

        while (!(scope instanceof ClassSymbol)) {
            scope = scope.getParent();
        }
        populateDispTable(listFunctions, ((ClassSymbol) scope).getName());

        List<ST> paramsSt = new ArrayList<>();
        for (var param : params) {
            paramsSt.add(param.accept(this));
        }
        Collections.reverse(paramsSt);

        var offset = findOffsetFunc(listFunctions, id);
        return templates.getInstanceOf("function_call")
                .add("cnt", dispatchCounter++)
                .add("line", call.getToken().getLine())
                .add("offset", offset)
                .add("params", paramsSt)
                .add("includeMove", true);
    }

    private static int findOffsetFunc(List<String> listFunctions, Id id) {
        int offset = -1;
        for (int i = 0; i < listFunctions.size(); i++) {
            var stripFunc = listFunctions.get(i).substring(listFunctions.get(i).indexOf('.') + 1);
            if (stripFunc.equals(id.getToken().getText())) {
                offset = i * 4;
                break;
            }
        }
        return offset;
    }

    @Override
    public ST visit(CallDispatch callDispatch) {
        var id = callDispatch.getId();
        var scope = id.getScope();
        var initScope = id.getScope();
        var e = callDispatch.getE();
        var exprs = callDispatch.getExprs();
        var type = callDispatch.getType();

        var retSt = e.accept(this);

        while (!(scope instanceof ClassSymbol)) {
            scope = scope.getParent();
        }

        List<ST> paramsSt = new ArrayList<>();
        for (var param : exprs) {
            paramsSt.add(param.accept(this));
        }

        Collections.reverse(paramsSt);

        if (type != null) {
            List<String> listFunctions = new ArrayList<>();

            populateDispTable(listFunctions, type.getToken().getText());
            var offset = findOffsetFunc(listFunctions, id);

            return templates.getInstanceOf("function_call_dispatch")
                    .add("cnt", dispatchCounter++)
                    .add("line", callDispatch.getToken().getLine())
                    .add("offset", offset)
                    .add("params", paramsSt)
                    .add("e", retSt)
                    .add("id", type.getToken().getText());
        }

        if (e.getToken().getText().equals("self")) {
            return visit(new Call(id, exprs, callDispatch.getToken(), callDispatch.parserContext));
        }

        List<String> listAttrsNames = new ArrayList<>();
        populateAttrNamesTable(listAttrsNames, ((ClassSymbol) scope).getName());
        List<String> listAttrs = new ArrayList<>();
        populateAttrTable(listAttrs, ((ClassSymbol) scope).getName());

        var offset = getOffsetAttr(listAttrsNames, e);
        if (offset == -1) {
            // nu e atribut al metodei, acum ar fi bun tipul si de acolo ma descurc i guess?
            List<String> listFunctions = new ArrayList<>();

            populateDispTable(listFunctions, callDispatch.getExprType().getName());
            offset = findOffsetFunc(listFunctions, id);

            if (callDispatch.getExprType().getName().equals(((ClassSymbol) scope).getName())) {
                return templates.getInstanceOf("function_call")
                        .add("cnt", dispatchCounter++)
                        .add("line", callDispatch.getToken().getLine())
                        .add("offset", offset)
                        .add("params", paramsSt)
                        .add("e", retSt)
                        .add("includeMove", false);
            }
            return templates.getInstanceOf("function_call_trick")
                    .add("cnt", dispatchCounter++)
                    .add("line", callDispatch.getToken().getLine())
                    .add("offset", offset)
                    .add("params", paramsSt)
                    .add("e", retSt);
        }

        List<String> listFunctions = new ArrayList<>();

        populateDispTable(listFunctions, callDispatch.getExprType().getName());
        var offset_method = findOffsetFunc(listFunctions, id);

        return templates.getInstanceOf("function_call_attr")
                .add("cnt", dispatchCounter++)
                .add("line", callDispatch.getToken().getLine())
                .add("offset", offset)
                .add("params", paramsSt)
                .add("e", retSt)
                .add("reg", retSt.getAttribute("reg"))
                .add("offset_method", offset_method);
    }

    private static int getOffsetAttr(List<String> listAttrsNames, Expression e) {
        for (int i = 0; i < listAttrsNames.size(); i++) {
            var attr = listAttrsNames.get(i);
            if (attr.equals(e.getToken().getText())) {
                return (i + 3) * 4;
            }
        }
        return -1;
    }

    @Override
    public ST visit(If anIf) {
        return templates.getInstanceOf("if")
                .add("condPath", anIf.getIfBranch().accept(this))
                .add("thenPath", anIf.getThenBranch().accept(this))
                .add("elsePath", anIf.getElseBranch().accept(this))
                .add("else_counter", univCounter++)
                .add("end_counter", univCounter++);
    }

    @Override
    public ST visit(While aWhile) {
        var cond = aWhile.getCondition().accept(this);
        var body = aWhile.getBody().accept(this);

        return templates.getInstanceOf("while")
                .add("cond", cond)
                .add("body", body)
                .add("while_counter", univCounter++)
                .add("end_counter", univCounter++);
    }

    @Override
    public ST visit(Case aCase) {
        var e = aCase.getExpression().accept(this);
        List <ST> ids = new ArrayList<ST>();
        List <Integer> lows = new ArrayList<>();
        List<Integer> ups = new ArrayList<>();
        var caseBranches = new ArrayList<>();
        var prevCaseBranches = new ArrayList<>();
        int initialCounter = univCounter++;
        for (var id : aCase.getIds()) {
            ids.add(id.accept(this));
            prevCaseBranches.add(univCounter - 1);
            caseBranches.add(univCounter++);
        }
        for (var type : aCase.getTypes()) {
            type.accept(this);
            lows.add(classHierarchy.get(type.getToken().getText()).get(0));
            ups.add(classHierarchy.get(type.getToken().getText()).get(1));
        }
        List <ST> exprs = new ArrayList<ST>();
        for (var expr : aCase.getExpressions()) {
            exprs.add(expr.accept(this));
        }

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < lows.size(); i++) {
            indices.add(i);
        }

        List<Integer> finalLows = lows;
        indices.sort((i, j) -> Integer.compare(lows.get(j), lows.get(i)));

        List<ST> sortedIds = new ArrayList<>();
        var sortedLows = new ArrayList<Integer>();
        var sortedUps = new ArrayList<Integer>();
        List<ST> sortedExprs = new ArrayList<ST>();

        for (int index : indices) {
            sortedIds.add(ids.get(index));
            sortedLows.add(lows.get(index));
            sortedUps.add(ups.get(index));
            sortedExprs.add(exprs.get(index));
        }

        prevCaseBranches.removeLast();
        caseBranches.removeLast();
        return templates.getInstanceOf("case")
                .add("line", aCase.getToken().getLine())
                .add("case_counters", caseBranches)
                .add("ids", sortedIds.subList(1, sortedIds.size()))
                .add("exprs", sortedExprs.subList(1, sortedExprs.size()))
                .add("lows", sortedLows.subList(1, sortedLows.size()))
                .add("ups", sortedUps.subList(1, sortedUps.size()))
                .add("prev_case_counters", prevCaseBranches)
                .add("last_case_counter", caseBranches.size() > 0 ? caseBranches.getLast() : null)
                .add("case_counter_init", initialCounter)
                .add("id_init", sortedIds.get(0))
                .add("low_init", sortedLows.get(0))
                .add("up_init", sortedUps.get(0))
                .add("expr_init", sortedExprs.get(0))
                .add("case_counter_next", initialCounter)
                .add("e", e)
                .add("endcase_counter", univCounter++);
    }

    @Override
    public ST visit(Block block) {
        var exprs = block.getExpressions();
        List<ST> sts = new ArrayList<>();
        ST blockTemplate = templates.getInstanceOf("block");
        for (var expr : exprs) {
            sts.add(expr.accept(this));
        }
        blockTemplate.add("vals", sts);
        return blockTemplate;
    }

    @Override
    public ST visit(Let let) {
        List<ST> values = new ArrayList<>();
        List<String> idxs = new ArrayList<>();
        for (int i = 0; i < let.getExpressions().size(); i++) {
            var expr = let.getExpressions().get(i);
            var id = let.getIds().get(i);
            var type = let.getTypes().get(i);

            if (expr == null) {
                values.add(type.accept(this));
            } else {
                values.add(expr.accept(this));
            }
            idxs.add(String.valueOf(-4 * ((((FieldSymbol)id.getSymbol()).getFieldIndex()) + 1)));
        }
        var body = let.getBody().accept(this);

        return templates.getInstanceOf("let")
                .add("params", values)
                .add("body", body)
                .add("offset_sub", -4 * let.getIds().size())
                .add("offset_add", 4 * let.getIds().size())
                .add("idxs", idxs);
    }
}
