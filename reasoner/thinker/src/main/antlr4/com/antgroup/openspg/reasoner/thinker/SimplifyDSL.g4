grammar SimplifyDSL;

import KGDSL;

script: (
		define_rule_on_concept
		| define_rule_on_relation_to_concept
		| define_proiority_rule_on_concept
)*;

/*
Define (患者状态/`缺少血肌酐数据`) {
	!血肌酐
}
*/
define_rule_on_concept : define_rule_on_concept_structure;

/*
Define [基本用药方案]->(药品/`ACEI+噻嗪类利尿剂`) {
  疾病/高血压 and 药品/多药方案
}
*/
define_rule_on_relation_to_concept : define_rule_on_relation_to_concept_structure;

/*
DefinePriority(危险水平分层) {
  超高危=100
  高危=80
  中危=50
  低危=10
}
*/
define_proiority_rule_on_concept : define_priority_rule_on_concept_structure;

define_rule_on_concept_structure:
    the_define_structure_symbol concept_declaration rule_and_action_body;

concept_declaration: left_paren concept_name right_paren;

concept_name : '`' single_level_concept_name (solidus single_level_concept_name)+ '`';

single_level_concept_name : EscapedSymbolicName;

define_rule_on_relation_to_concept_structure:
    the_define_structure_symbol rule_name_declaration right_arrow concept_declaration rule_body;

rule_name_declaration : left_bracket identifier right_bracket ;

the_define_priority_symbol : DEFINE_PRIORITY;
define_priority_rule_on_concept_structure:
    the_define_priority_symbol priority_declaration assiginment_structure;

priority_declaration: left_paren identifier right_paren;

rule_and_action_body: left_brace rule_body_content (action_body_structure)? right_brace;

rule_body: left_brace rule_body_content right_brace;

rule_body_content : logical_statement*;

logical_statement : rule_expression (logical_connect_operator rule_expression)*;

rule_expression : (not_operator)? rule_statement;

rule_statement : calculate_expression;

action_body_structure : create_action_symbol assiginment_structure;

assiginment_structure : left_brace muliti_assignment_statement right_brace;

muliti_assignment_statement : assignment_statement*;

assignment_statement : identifier assignment_operator logical_statement;

calculate_expression : value_expression;

not_operator : NOT_Symb;

logical_connect_operator : AND | OR;

bracket_right_arrow : ;

right_bracket_minus : ;

DEFINE_PRIORITY : 'DefinePriority' ;