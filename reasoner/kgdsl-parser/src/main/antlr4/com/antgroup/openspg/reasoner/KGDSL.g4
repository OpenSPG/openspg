/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */
grammar KGDSL;

//#############################################################################
// 词法分析
//#############################################################################
//#############################################################################
// 应用模式
//#############################################################################
//计算模式
//    GraphStruture {
//        path1: (s)-[p1:x]->(end:y)
//    }
//    Rule {
//        R1('xxx'): path1
//    }
//    Action {
//
//    }
base_rule_define : the_graph_structure the_rule create_action?;

// define 模式
//    Define (s:label1)-[p:label2]->[o:concept/ceontep1] {
//        GraphStruture {
//            path1: (s)-[p1:x]->(end:y)
//        }
//        Rule {
//            R1('xxx'): path1
//        }
//        Action {
//
//        }
//    }
base_perdicated_define : the_define_structure ;
// kgdsl 1.0 兼容模式
//    GraphStruture {
//        path1: (s)-[p1:x]->(end:y)
//    }
//    Rule {
//        R1('xxx'): path1
//    }
//    Action {
//        get(s.name)
//    }
kgdsl_old_define : the_graph_structure the_rule the_action? ;
// 多行模式，支持多个define，最后处理
kg_dsl
    : base_perdicated_define* base_job? EOF
    ;
base_job
    : kgdsl_old_define
    | gql_query_statement
    ;

//#############################################################################
// Define 定义部分
//#############################################################################
the_define_structure
    : DEFINE perdicated_define '{' base_rule_define '}'
    ;
perdicated_define
    : node_pattern full_edge_pointing_right  node_pattern
    ;
//#############################################################################
// graph structure 定义部分
//#############################################################################
demo_graph
    : GRAPH '{' graph_structure_body '}'
    ;

the_graph_structure
    : graph_structure_head '{' graph_structure_define? '}'
    ;

graph_structure_head
    : GRAPH_STRUCTURE
    | STRUCTURE
    ;
graph_structure_define
    : graph_structure_body
    | path_pattern_list
    ;
// path pattern expression, for kgdsl 1.0
graph_structure_body
    : graph_structure_one_line+
    ;

graph_structure_one_line
    : define_edge
    | define_vertex
    ;

// 定义边 start
define_edge
    : define_one_edge
    ;

define_one_edge
    : vertex_from right_arrow vertex_to ('[' label_property_list ']')? (REPEAT repeat_time)? ('as' edge_name)?
    | vertex_from both_arrow vertex_to ('[' label_property_list ']')? (REPEAT repeat_time)? ('as' edge_name)?
    ;

repeat_time
    : ('('lower_bound ',' upper_bound')')
    ;

vertex_from
    : vertex_name
    ;

vertex_to
    : vertex_name
    ;

edge_name
    : identifier
    ;
// 定义边 end


// 定义点 start
define_vertex
    : define_one_vertex
    | define_multiple_vertex
    ;

define_one_vertex
    : vertex_name ('[' label_property_list ']')?
    ;

define_multiple_vertex
    : vertex_name (',' vertex_name)* ('[' label_property_list ']')?
    ;

vertex_name
    : identifier
    ;

label_property_list
    : label_name (',' label_name)* (',' property_key '=' property_value)*
    | property_key '=' property_value (',' property_key '=' property_value)*
    ;

// 尝试匹配路径
path_property_list
    : 'Path' '=' '{' graph_structure_body '}'
    ;


property_key
    : identifier
    ;

property_value
    : oC_NumberLiteral
    | oC_SymbolicName
    | character_string_literal
    | parameter_value_specification
    ;

// path pattern expression, for iso gql
// path1: abc\n path2: a
path_pattern_list : path_pattern+ ;
path_pattern : (path_condition? path_variable colon )? path_pattern_expression ;
path_condition : OPTIONAL ;
path_variable : identifier ;
path_pattern_expression : element_pattern (comma element_pattern)* ;
element_pattern : node_pattern|one_edge_pattern ;
node_pattern : left_paren element_pattern_declaration_and_filler right_paren ;
one_edge_pattern: node_pattern (edge_pattern  node_pattern)+ ;
edge_pattern : (full_edge_pattern|abbreviated_edge_pattern) graph_pattern_quantifier?;

full_edge_pattern : full_edge_pointing_right|full_edge_pointing_left|full_edge_any_direction ;
full_edge_pointing_right : minus_left_bracket element_pattern_declaration_and_filler (edge_pattern_pernodelimit_clause)? bracket_right_arrow ;
full_edge_pointing_left : left_arrow_bracket element_pattern_declaration_and_filler (edge_pattern_pernodelimit_clause)? right_bracket_minus ;
full_edge_any_direction : minus_left_bracket element_pattern_declaration_and_filler (edge_pattern_pernodelimit_clause)? right_bracket_minus;

edge_pattern_pernodelimit_clause : per_node_limit oC_IntegerLiteral ;
per_node_limit : PER_NODE_LIMIT ;
graph_pattern_quantifier : question_mark|quantifier ;
quantifier : left_brace ( lower_bound )? comma ( upper_bound )? right_brace ;
lower_bound : oC_IntegerLiteral ;
upper_bound : oC_IntegerLiteral ;
abbreviated_edge_pattern : right_arrow|left_arrow|minus_sign ;

element_pattern_declaration_and_filler : ( element_variable_declaration )? ( colon (label_expression | linked_edge) )? ( element_pattern_where_clause )? ;
element_variable_declaration : element_variable ;
element_variable : identifier ;
label_expression : label_name (vertical_bar label_name)* ;
label_name : entity_type | concept_name;
entity_type : identifier | prefix_name;
prefix_name : identifier period identifier ;
concept_name : meta_concept_type solidus concept_instance_id ;
meta_concept_type : identifier ;
concept_instance_id : identifier ;

linked_edge : function_expr;

element_pattern_where_clause : WHERE search_condition ;
search_condition : logic_value_expression ;

//#############################################################################
// rule 定义部分
//#############################################################################
the_rule
    : rule_head '{' rule_expression_body '}'
    ;

rule_head
    : CONSTRAINT
    | RULE
    ;
rule_expression_body : rule_expression* ;
// rule expression
rule_expression : project_rule_expression | logic_rule_expression;
// project rule
project_rule_expression : identifier (period property_name ) ? explain? assignment_operator expression_set;
// logic rule
logic_rule_expression : identifier explain? colon expression_set;

// rule explain
explain : left_paren unbroken_character_string_literal right_paren;

expression_set : value_expression|list_op_express|graph_group_op_express;
// 表达式
value_expression : logic_value_expression|project_value_expression ;

// list的聚合语法，支持链式表达风格 {variable}.op(k:do(k))?*
list_op_express : value_expression (period list_op)* ;

list_op :
    list_common_agg_express|
    list_common_agg_if_express|
    list_filter_op_name|
    list_limit_op|
    list_get_op|
    list_slice_op|
    list_str_join_op|
    list_accumulate_op
    ;

list_common_agg_express : list_common_agg_name left_paren list_op_args right_paren;
list_op_args:
    |
    value_expression;


list_common_agg_name :
    SUM|
    AVG|
    COUNT|
    MIN|
    MAX;

list_common_agg_if_name :
    SUMIF|
    AVGIF|
    COUNTIF|
    MINIF|
    MAXIF;

order_op_name:
    DESC|ASC;


list_filter_op_name : IF left_paren list_op_args right_paren;

list_common_agg_if_express:
    list_common_agg_if_chain_express
    | list_common_agg_if_one_express
    ;

list_common_agg_if_chain_express:
    list_filter_op_name period list_common_agg_express;

list_common_agg_if_one_express:
    list_common_agg_if_name left_paren list_op_args (comma list_op_args)? right_paren ;

list_order_op:
     order_op_name left_paren list_op_args right_paren;


list_limit_op :
    list_limit_op_all|
    list_order_and_limit;

list_limit_op_all :
    LIMIT left_paren  oC_IntegerLiteral right_paren;

list_order_and_limit:
    list_order_op period list_limit_op_all;

list_get_op : GET left_paren  oC_IntegerLiteral right_paren;

list_slice_op : SLICE left_paren  oC_IntegerLiteral comma oC_IntegerLiteral right_paren;

list_str_join_op : 'str_join' left_paren character_string_literal right_paren;
accumulate_support_op : plus_sign|asterisk;
list_accumulate_op : 'accumulate' left_paren accumulate_support_op right_paren;

//group的聚合风格 group(a,c).op(expr)
graph_group_op_express : 'group' left_paren graph_alias_element_list right_paren (period graph_op )*;

graph_op:
    graph_common_agg_udf_express|
    graph_common_agg_express|
    graph_common_agg_if_express|
    graph_order_and_slice_op|
    graph_filter_op
    ;
graph_common_agg_udf_express : function_name left_paren graph_alias (period property_name)? comma function_args? right_paren;
graph_common_agg_express : graph_common_agg_name left_paren graph_alias (period property_name)? right_paren;
graph_common_agg_name :
    SUM|
    AVG|
    COUNT|
    MIN|
    MAX|
    ;

graph_common_agg_if_name :
    SUMIF|
    AVGIF|
    COUNTIF|
    MINIF|
    MAXIF;

graph_common_agg_if_express :
    graph_common_agg_if_chain_express
    | graph_common_agg_if_one_express
    ;

graph_common_agg_if_chain_express :
    graph_filter_op period graph_common_agg_express;

graph_common_agg_if_one_express :
    graph_common_agg_if_name left_paren value_expression comma graph_alias (period property_name)? right_paren;

graph_order_op : order_op_name left_paren graph_alias (period property_name)? right_paren;
graph_order_and_slice_op: graph_order_op period graph_limit_op;
graph_limit_op : LIMIT left_paren oC_IntegerLiteral right_paren;
graph_filter_op : IF left_paren value_expression right_paren;
graph_alias : identifier ;
graph_alias_element_list : graph_alias ( comma graph_alias )* ;


//#############################################################################
// graph structure 定义部分
//#############################################################################
// action 关键字
ACTION_GET : 'get' | 'distinctGet' ;
ADD_EDGE : 'createEdgeInstance' ;
ADD_NODE : 'createNodeInstance';
SQL : 'sql';
COMMENT : 'COMMENT';
EMBEDDED_SQL_ACTION
    : '>>>' .* '<<<'
    ;

create_action
    : ACTION '{' create_action_body* '}'
    ;
create_action_body
    : add_node
    | add_edge
    ;
add_edge
    : ADD_EDGE '('  add_edge_param comma  add_edge_param comma add_type comma add_props')'
    ;
add_type
    : identifier assignment_operator label_expression
    ;
add_edge_param
    : identifier assignment_operator identifier
    ;
add_props
    : identifier assignment_operator complex_obj_expr
    ;
add_node
    : (identifier assignment_operator)? ADD_NODE '(' add_type comma add_props ')'
    ;
the_action
    : ACTION '{' action_body? '}'
    ;
action_body
    : get_action
    ;

one_element_in_get
    : one_element_with_const
    | one_element_with_variable
    ;

one_element_with_variable
    : non_parenthesized_value_expression_primary_with_property ((AS|'as') as_alias_with_comment)?
    ;

one_element_with_const
    : unbroken_character_string_literal (AS|'as') as_alias_with_comment
    ;

as_alias_with_comment
    : identifier (COMMENT unbroken_character_string_literal)?
    ;
as_view_in_get
    : (AS|'as') '(' identifier '(' as_alias_with_comment (',' as_alias_with_comment)* ')' ')'
    | (AS|'as') '(' as_alias_with_comment (',' as_alias_with_comment)* ')'
    ;

sql_in_get
    : SQL '(' EMBEDDED_SQL_ACTION ')'
    ;

/** get语法举例
get( A.id        as a_id              COMMENT '一个id'
    ,B.type      as b_type                               // 可以没有COMMENT
    ,D.name      as d_name            COMMENT 'd的名称'
    ,subgraph(C,neighbor=100,degree=1) as (c_subgraph_part0 COMMENT 'c子图第一部分', c_subgraph_part1 COMMENT 'c子图第二部分')
    ,subgraph(D,neighbor=100,degree=2) as d_subgraph   // 不切分，无COMMENT
    )
.sql(>>>
    select a_id, b_type, c_subgraph_part0, c_subgraph_part1
    from view // 默认名称是view
<<<)
*/
get_action
    // 一般的get
    : ACTION_GET '(' one_element_in_get (',' one_element_in_get)*')' ('.' sql_in_get)?
    // TODO 以下两种语法后续将慢慢废弃掉
    | ACTION_GET '(' one_element_in_get (',' one_element_in_get)* ')' ('.' as_view_in_get )?
    | ACTION_GET '(' one_element_in_get (',' one_element_in_get)*  ')' '.' as_view_in_get '.' sql_in_get
    ;

//#############################################################################
// 公共 定义部分
//#############################################################################
// 全局关键字
GRAPH_STRUCTURE : 'GraphStructure' ;
STRUCTURE : ('S')('T'|'t')('R'|'r')('U'|'u')('C'|'c')('T'|'t')('U'|'u')('R'|'r')('E'|'e');
GRAPH : 'Graph' ;
RULE : 'Rule' ;
CONSTRAINT : ('C')('O'|'o')('N'|'n')('S'|'s')('T'|'t')('R'|'r')('A'|'a')('I'|'i')('N'|'n')('T'|'t');
ACTION : 'Action' ;
DEFINE : 'Define' ;
// 不区分大小写的关键词
GET : ('G'|'g')('E'|'e')('T'|'t');
SLICE : ('S'|'s')('L'|'l')('I'|'i')('C'|'c')('E'|'e');
SUM : ('S' | 's')('U' | 'u')('M' | 'm') ;
AVG : ('A' | 'a')('V' | 'v')('G' | 'g') ;
COUNT : ('C' | 'c')('O' | 'o')('U' | 'u')('N' | 'n')('T' | 't') ;
MIN : ('M' | 'm')('I' | 'i')('N' | 'n') ;
MAX : ('M' | 'm')('A' | 'a')('X' | 'x') ;
SUMIF : ('S' | 's')('U' | 'u')('M' | 'm')('I' | 'i')('F' | 'f') ;
AVGIF : ('A' | 'a')('V' | 'v')('G' | 'g')('I' | 'i')('F' | 'f') ;
COUNTIF : ('C' | 'c')('O' | 'o')('U' | 'u')('N' | 'n')('T' | 't')('I' | 'i')('F' | 'f') ;
MINIF : ('M' | 'm')('I' | 'i')('N' | 'n')('I' | 'i')('F' | 'f') ;
MAXIF : ('M' | 'm')('A' | 'a')('X' | 'x')('I' | 'i')('F' | 'f') ;
IN  : ('I' | 'i')('N' | 'n') ;
LIKE : ('L' | 'l')('I' | 'i')('K' | 'k')('E' | 'e');
RLIKE : ('R' | 'r')('L' | 'l')('I' | 'i')('K' | 'k')('E' | 'e');
ALL : ('A' | 'a')('L' | 'l')('L' | 'l') ;
IF : ('I' | 'i')('F' | 'f') ;
DISTINCT : ('D' | 'd')('I' | 'i')('S' | 's')('T' | 't')('I' | 'i')('N' | 'n')('C' | 'c')('T' | 't') ;
GROUP : ('G' | 'g')('R' | 'r')('O' | 'o')('U' | 'u')('P' | 'p') ;
IS : ('I' | 'i')('S' | 's') ;

DESC : ('D' | 'd')('E' | 'e')('S' | 's')('C' | 'c') ;
ASC :  ('A' | 'a')('S' | 's')('C' | 'c');
LIMIT : ('L' | 'l')('I' | 'i')('M' | 'm')('I' | 'i')('T' | 't');
OFFSET :('O' | 'o')('F' | 'f')('F' | 'f')('S' | 's')('E' | 'e')('T' | 't');

AND : (('A' | 'a')('N' | 'n')('D' | 'd'))|('&&') ;
OR_Latter : ('O' | 'o')('R' | 'r') ;
OR_Symb : '||';

NOT : ('N'|'n')('O'|'o')('T'|'t');
TRUE : ('T' | 't')('R' | 'r')('U' | 'u')('E' | 'e') ;
FALSE : ('F' | 'f')('A' | 'a')('L' | 'l')('S' | 's')('E' | 'e') ;
NULL : ('N'|'n')('U'|'u')('L'|'l')('L'|'l');

PER_NODE_LIMIT : ('P' | 'p')('E' | 'e')('R' | 'r') '_' ('N' | 'n')('O' | 'o')('D' | 'd')('E' | 'e') '_' ('L' | 'l')('I' | 'i')('M' | 'm')('I' | 'i')('T' | 't') ;
AS : ('A' | 'a')('S' | 's') ;
OPTIONAL : ('O' | 'o')('P' | 'p')('T' | 't')('I' | 'i')('O' | 'o')('N' | 'n')('A' | 'a')('L' | 'l') ;

REPEAT : 'repeat' ;
WHERE : ('W' | 'w')('H' | 'h')('E' | 'e')('R' | 'r')('E' | 'e') ;
MATCH : ('M' | 'm')('A' | 'a')('T' | 't')('C' | 'c')('H' | 'h') ;
RETURN : ('R' | 'r')('E' | 'e')('T' | 't')('U' | 'u')('R' | 'r')('N' | 'n') ;
// rule 表达式
or : OR_Latter|OR_Symb;
value_expression_primary : parenthesized_value_expression|non_parenthesized_value_expression_primary_with_property ;
parenthesized_value_expression : left_paren value_expression right_paren ;
non_parenthesized_value_expression_primary_with_property: non_parenthesized_value_expression_primary (period property_name ) * ;
non_parenthesized_value_expression_primary :
    binding_variable|
    function_expr|
    unsigned_value_specification;
property_name : identifier ;

binding_variable : binding_variable_name ;
binding_variable_name : identifier ;
identifier : oC_SymbolicName ;

unsigned_value_specification : unsigned_literal|parameter_value_specification ;
unsigned_literal : unsigned_numeric_literal|general_literal ;
general_literal : predefined_type_literal|list_literal ;
predefined_type_literal : boolean_literal|character_string_literal ;
boolean_literal : truth_value;
character_string_literal : unbroken_character_string_literal ( separator unbroken_character_string_literal )* ;

parameter_value_specification : dollar_sign identifier;

list_literal : '[' list_element_list ']';
list_element_list : list_element ( comma list_element )* ;
list_element : value_expression ;


// rule operator
expr : binary_expr|unary_expr|function_expr ;
binary_expr : project_value_expression (binary_op project_value_expression)? ;
binary_op :
    assignment_operator|
    equals_operator|
    not_equals_operator|
    less_than_operator|
    greater_than_operator|
    less_than_or_equals_operator|
    greater_than_or_equals_operator|
    like_operator |
    rlike_operator |
    in_operator ;
unary_expr : unary_op left_paren value_expression right_paren;

unary_op:
    exist_operator|
    abs_operator|
    floor_operator|
    ceiling_operator;


ABS : ('A'|'a')('B'|'b')('S'|'s');
FLOOR : ('FLOOR'|'floor');
CEIL : ('CEIL'|'ceil');
CEILING : ('CEILING'|'ceiling');

exist_operator : 'exist'|'EXIST';
abs_operator: ABS;
floor_operator: FLOOR;
ceiling_operator: CEIL|CEILING;

function_expr : function_name left_paren function_args? right_paren;
function_name : identifier;
function_args : list_element_list;


// 逻辑 计算
logic_value_expression : logic_term (or logic_term)* ;
logic_term : logic_factor (AND logic_factor)* ;
logic_factor : ( NOT )? logic_test ;
logic_test : expr ( (IS ( NOT )?|equals_operator|not_equals_operator) truth_value )? ;
truth_value : TRUE|FALSE|NULL ;


// 数值计算
unsigned_numeric_literal : oC_NumberLiteral ;
sign : plus_sign|minus_sign ;

complex_obj_expr : left_brace assignment_expression* right_brace;

assignment_expression : identifier assignment_operator expression_set;

project_value_expression : term (plus_sign term| minus_sign term) * ;
term : factor (asterisk factor| solidus factor| percent factor)* ;
factor : ( sign )? project_primary ;
project_primary : value_expression_primary|numeric_value_function ;

//数值计算函数
numeric_value_function :
    absolute_value_expression|
    floor_function|
    ceiling_function;

absolute_value_expression : abs_operator left_paren project_value_expression right_paren ;
floor_function : floor_operator left_paren project_value_expression right_paren ;
ceiling_function : ceiling_operator left_paren project_value_expression right_paren ;


// ISO GQL
gql_query_statement : match_statement return_statement ;
match_statement : MATCH graph_pattern ;
graph_pattern : path_pattern_list ( element_pattern_where_clause )? ;
return_statement : RETURN return_statement_body ;
return_statement_body : return_item_list ;
return_item_list : return_item ( comma return_item )* ;
return_item : value_expression ( return_item_alias )? ;
return_item_alias : (AS|'as') identifier ;


// 基础字符定义
dsl_special_character : space|ampersand|asterisk|colon|colon_equals|comma|dollar_sign|double_quote|double_colon|equals_operator|exclamation_mark|grave_accent|greater_than_operator|left_brace|left_bracket|left_paren|less_than_operator|minus_sign|period|plus_sign|question_mark|quote|reverse_solidus|right_brace|right_bracket|right_paren|semicolon|solidus|underscore|vertical_bar|percent|circumflex ;
// other_language_character : 'TODO_OTHER_LANGUAGE_CHARACTER' ;
grave_accent : '`' ;
bracket_right_arrow : ']->' ;
concatenation_operator : '||' ;
multiset_alternation_operator : '|+|' ;
like_operator : LIKE ;
rlike_operator : RLIKE ;
in_operator: IN ;
greater_than_or_equals_operator : '>=' ;
left_arrow : '<-' ;
less_than_or_equals_operator : '<=' ;
not_equals_operator : '<>'|'!=' ;
right_arrow : '->' ;
both_arrow : '<->' ;
left_arrow_bracket : '<-[' ;
minus_left_bracket : '-[' ;
right_bracket_minus : ']-' ;
bracketed_comment_introducer : '/*' ;
bracketed_comment_terminator : '*/' ;
escaped_grave_accent : '``' ;
ampersand : '&' ;
asterisk : '*' ;
circumflex : '^' ;
colon_equals : ':=' ;
colon : ':' ;
comma : ',' ;
Newline : LF -> skip;
space : 'TODO_SPACE' ;
dollar_sign : '$' ;
double_colon : '::' ;
double_quote : '"' ;
equals_operator : '==' ;
assignment_operator : '=';
exclamation_mark : '!' ;
greater_than_operator : '>' ;
left_brace : '{' ;
left_bracket : '[' ;
left_paren : '(' ;
less_than_operator : '<' ;
minus_sign : '-' ;
double_minus_sign : '--' ;
percent : '%' ;
period : '.' ;
double_period : '..' ;
plus_sign : '+' ;
question_mark : '?' ;
quote : '\'' ;
reverse_solidus : '\\' ;
right_brace : '}' ;
right_bracket : ']' ;
right_paren : ')' ;
semicolon : ';' ;
solidus : '/' ;
double_solidus : '//' ;
underscore : '_' ;
vertical_bar : '|' ;

separator : ( comment|whitespace )+ ;
whitespace : WHITESPACE ;
comment : Comment;

// 完整字符串定义
unbroken_character_string_literal : StringLiteral ;

StringLiteral
             :  ( '"' ( StringLiteral_0 | EscapedChar )* '"' )
                 | ( '\'' ( StringLiteral_1 | EscapedChar )* '\'' )
                 ;

EscapedChar
           :  '\\' ( '\\' | '\'' | '"' | ( 'B' | 'b' ) | ( 'F' | 'f' ) | ( 'N' | 'n' ) | ( 'R' | 'r' ) | ( 'T' | 't' ) | ( ( 'U' | 'u' ) ( HexDigit HexDigit HexDigit HexDigit ) ) | ( ( 'U' | 'u' ) ( HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit ) ) ) ;

// 整型的定义
oC_IntegerLiteral
              :  HexInteger
                  | OctalInteger
                  | DecimalInteger
                  ;

HexInteger
          :  '0x' ( HexDigit )+ ;

DecimalInteger
              :  ZeroDigit
                  | ( NonZeroDigit ( Digit )* )
                  ;

OctalInteger
            :  ZeroDigit ( OctDigit )+ ;

HexLetter
         :  ( 'A' | 'a' )
             | ( 'B' | 'b' )
             | ( 'C' | 'c' )
             | ( 'D' | 'd' )
             | ( 'E' | 'e' )
             | ( 'F' | 'f' )
             ;

HexDigit
        :  Digit
            | HexLetter
            ;

Digit
     :  ZeroDigit
         | NonZeroDigit
         ;

NonZeroDigit
            :  NonZeroOctDigit
                | '8'
                | '9'
                ;

NonZeroOctDigit
               :  '1'
                   | '2'
                   | '3'
                   | '4'
                   | '5'
                   | '6'
                   | '7'
                   ;

OctDigit
        :  ZeroDigit
            | NonZeroOctDigit
            ;

ZeroDigit
         :  '0' ;

// 数字类型定义
oC_NumberLiteral
             :  oC_DoubleLiteral
                 | oC_IntegerLiteral
                 ;

// 浮点数定义
oC_DoubleLiteral
             :  ExponentDecimalReal
                 | RegularDecimalReal
                 ;

ExponentDecimalReal
                   :  ( ( Digit )+ | ( ( Digit )+ '.' ( Digit )+ ) | ( '.' ( Digit )+ ) ) ( 'E' | 'e' ) '-'? ( Digit )+ ;

RegularDecimalReal
                  :  ( Digit )* '.' ( Digit )+ ;

// 符号变量定义
oC_SymbolicName
            :  UnescapedSymbolicName
                | EscapedSymbolicName
                | HexLetter;
identifier_body :  oC_SymbolicName;
UnescapedSymbolicName
                     :  IdentifierStart ( IdentifierPart )* ;

/**
 * Based on the unicode identifier and pattern syntax
 *   (http://www.unicode.org/reports/tr31/)
 * And extended with a few characters.
 */
IdentifierStart
               :  ID_Start
                   | Pc
                   ;

/**
 * Based on the unicode identifier and pattern syntax
 *   (http://www.unicode.org/reports/tr31/)
 * And extended with a few characters.
 */
IdentifierPart
              :  ID_Continue
                  | Sc
                  ;

/**
 * Any character except "`", enclosed within `backticks`. Backticks are escaped with double backticks.
 */
EscapedSymbolicName
                   :  ( '`' ( EscapedSymbolicName_0 )* '`' )+ ;

SP
  :  ( WHITESPACE )+ -> skip;

WHITESPACE
          :  SPACE
              | TAB
              | LF
              | VT
              | FF
              | CR
              | FS
              | GS
              | RS
              | US
              | '\u1680'
              | '\u180e'
              | '\u2000'
              | '\u2001'
              | '\u2002'
              | '\u2003'
              | '\u2004'
              | '\u2005'
              | '\u2006'
              | '\u2008'
              | '\u2009'
              | '\u200a'
              | '\u2028'
              | '\u2029'
              | '\u205f'
              | '\u3000'
              | '\u00a0'
              | '\u2007'
              | '\u202f'
              | Comment
              ;

Comment
       :  ( '/*' ( Comment_1 | ( '*' Comment_2 ) )* '*/' )
           | ( '//' ( Comment_3 )* CR? ( LF | EOF ) )
           ;

oC_LeftArrowHead
             :  '<'
                 | '\u27e8'
                 | '\u3008'
                 | '\ufe64'
                 | '\uff1c'
                 ;

oC_RightArrowHead
              :  '>'
                  | '\u27e9'
                  | '\u3009'
                  | '\ufe65'
                  | '\uff1e'
                  ;

oC_Dash
    :  '-'
        | '\u00ad'
        | '\u2010'
        | '\u2011'
        | '\u2012'
        | '\u2013'
        | '\u2014'
        | '\u2015'
        | '\u2212'
        | '\ufe58'
        | '\ufe63'
        | '\uff0d'
        ;

fragment FF : [\f] ;

fragment EscapedSymbolicName_0 : ~[`] ;

fragment RS : [\u001E] ;

fragment ID_Continue : [\p{ID_Continue}] ;

fragment Comment_1 : ~[*] ;

fragment StringLiteral_1 : ~['\\] ;

fragment Comment_3 : ~[\n\r] ;

fragment Comment_2 : ~[/] ;

fragment GS : [\u001D] ;

fragment FS : [\u001C] ;

fragment CR : [\r] ;

fragment Sc : [\p{Sc}] ;

fragment SPACE : [ ] ;

fragment Pc : [\p{Pc}] ;

fragment TAB : [\t] ;

fragment StringLiteral_0 : ~["\\] ;

fragment LF : [\n] ;

fragment VT : [\u000B] ;

fragment US : [\u001F] ;

fragment ID_Start : [\p{ID_Start}] ;