(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["e3e7869b"],{"0b69d691":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{getEntityDetail:function(){return r;},getEnumValues:function(){return a;},getLlmSelect:function(){return i;},getOneHopGraph:function(){return d;},getSampleData:function(){return s;},search:function(){return l;}});var o=n("05ecbb5b");async function r(e,t){return(0,o.request)("/v1/datas/getEntityDetail",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}async function a(e,t){let{name:n}=e;return(0,o.request)(`/v1/datas/getEnumValues/${n}`,{method:"GET",params:{...e},...t||{}});}async function i(e,t){return(0,o.request)("/v1/datas/getLlmSelect",{method:"GET",params:{...e},...t||{}});}async function d(e,t){return(0,o.request)("/v1/datas/getOneHopGraph",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}async function s(e,t){return(0,o.request)("/v1/datas/getSampleData",{method:"GET",params:{...e},...t||{}});}async function l(e,t){return(0,o.request)("/v1/datas/search",{method:"GET",params:{...e},...t||{}});}},"1a998156":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{CONSTRAINT_TYPE:function(){return i;},INFO_COLUMN:function(){return o;},RELATION_CATEGORY:function(){return d;},RELATION_DIRECTION:function(){return a;},RULE_TYPE:function(){return r;},baseSchemePropertyNameMap:function(){return v;},infoColumnMapping:function(){return h;},relationDirectionMapping:function(){return m;},ruleTypeMapping:function(){return p;}});var o,r,a,i,d,s,l,c,u,f,g=n("777fffbe")._(n("f02131d0"));(s=o||(o={}))[s.NAME=0]="NAME",s[s.DESCRIPTION=1]="DESCRIPTION",s[s.MODIFY_TIME=2]="MODIFY_TIME",s[s.KNOWLEDGE_COUNT=3]="KNOWLEDGE_COUNT",s[s.SOURCE=4]="SOURCE",s[s.TARGET=5]="TARGET",s[s.RELATION_DIRECTION=6]="RELATION_DIRECTION";let h={0:g.default.get({id:"spg.src.constants.knowledgeBuild.Name",dm:"\u540D\u79F0"}),1:g.default.get({id:"spg.src.constants.knowledgeBuild.Description",dm:"\u63CF\u8FF0"}),2:g.default.get({id:"spg.src.constants.knowledgeBuild.LastEditTime",dm:"\u6700\u8FD1\u7F16\u8F91\u65F6\u95F4"}),3:g.default.get({id:"spg.src.constants.knowledgeBuild.CumulativeQuantityOfKnowledge",dm:"\u77E5\u8BC6\u7D2F\u79EF\u6570\u91CF"}),4:g.default.get({id:"spg.src.constants.knowledgeBuild.StartingPoint",dm:"\u8D77\u70B9"}),5:g.default.get({id:"spg.src.constants.knowledgeBuild.End",dm:"\u7EC8\u70B9"}),6:g.default.get({id:"spg.src.constants.knowledgeBuild.RelationshipType",dm:"\u5173\u7CFB\u7C7B\u578B"})};(l=r||(r={})).RELATION="RELATION",l.PROPERTY="PROPERTY",l.CONCEPT="CONCEPT";let p={RELATION:{name:"RELATION",nameZh:g.default.get({id:"spg.src.constants.knowledgeBuild.RelationshipRules",dm:"\u5173\u7CFB\u89C4\u5219"})},PROPERTY:{name:"PROPERTY",nameZh:g.default.get({id:"spg.src.constants.knowledgeBuild.AttributeRules",dm:"\u5C5E\u6027\u89C4\u5219"})},CONCEPT:{name:"CONCEPT",nameZh:g.default.get({id:"spg.src.constants.knowledgeBuild.ConceptRules",dm:"\u6982\u5FF5\u89C4\u5219"})}};(c=a||(a={})).TRANSITIVE="TRANSITIVE",c.BOTH="BOTH",c.SINGLE="SINGLE",c.NULL="NULL";let m={TRANSITIVE:g.default.get({id:"spg.src.constants.knowledgeBuild.TransferRelationship",dm:"\u4F20\u9012\u5173\u7CFB"}),BOTH:g.default.get({id:"spg.src.constants.knowledgeBuild.SymmetricRelation",dm:"\u5BF9\u79F0\u5173\u7CFB"}),SINGLE:g.default.get({id:"spg.src.constants.knowledgeBuild.OneWayRelationship",dm:"\u5355\u5411\u5173\u7CFB"}),NULL:g.default.get({id:"spg.src.constants.knowledgeBuild.None",dm:"\u65E0"})};(u=i||(i={})).UNIQUE="UNIQUE",u.REQUIRE="REQUIRE",u.ENUM="ENUM",u.MAXIMUM_LT_OE="MAXIMUM_LT_OE",u.MAXIMUM_LT="MAXIMUM_LT",u.MINIMUM_GT_OE="MINIMUM_GT_OE",u.MINIMUM_GT="MINIMUM_GT",u.REGULAR="REGULAR",u.MULTIVALUE="MULTIVALUE",(f=d||(d={})).BASIC="BASIC_TYPE",f.CONCEPT="CONCEPT_TYPE",f.ENTITY="ENTITY_TYPE",f.EVENT="EVENT_TYPE",f.STANDARD="STANDARD_TYPE",f.ONE_LEVEL="ONE_LEVEL_TYPE";let v={id:"ID",name:g.default.get({id:"spg.src.constants.knowledgeBuild.Name",dm:"\u540D\u79F0"}),description:g.default.get({id:"spg.src.constants.knowledgeBuild.Description",dm:"\u63CF\u8FF0"}),__from_id__:g.default.get({id:"spg.src.constants.knowledgeBuild.StartingPoint",dm:"\u8D77\u70B9"}),__to_id__:g.default.get({id:"spg.src.constants.knowledgeBuild.End",dm:"\u7EC8\u70B9"})};},"1ebf6c5c":function(e,t,n){"use strict";var o=n("852bbaa9")._;n.d(t,"__esModule",{value:!0}),n.e(t,{Editor:function(){return h;},MonacoEditor:function(){return f;},default:function(){return p;}});var r=n("777fffbe"),a=n("852bbaa9"),i=n("32b7a2cf"),d=r._(n("f02131d0")),s=n("5b5ed4a9"),l=a._(n("4d0e37ae")),c=r._(n("1e70bad6"));n("5a70f60d");let u=l.default.lazy(()=>Promise.all([n.ensure("lib_monaco-editor"),n.ensure("7581f1ef")]).then(n.dr(o,n.bind(n,"7581f1ef")))),f=e=>(0,i.jsx)(l.Suspense,{fallback:(0,i.jsx)(c.default,{size:"small"}),children:(0,i.jsx)(u,{...e})}),g=s.styled.div`
  position: relative;
  .monaco-placeholder {
    position: absolute;
    display: none;
    white-space: pre-wrap;
    top: 0px;
    left: 38px;
    color: var(--deep-blue-47);
    font-size: 13px;
    pointer-events: none;
    user-select: none;
    line-height: 20px;
  }

  .decorationsOverviewRuler {
    display: none !important;
  }

  .scroll-decoration {
    box-shadow: none !important;
  }

  .monaco-editor {
    outline-width: 0 !important;
  }
`,h=({language:e,autoSize:t=!1,code:n,setCode:o,placeholder:r=d.default.get({id:"spg.components.Editor.PleaseEnter",dm:"\u8BF7\u8F93\u5165"}),options:a={},style:s,className:c,theme:u="light",height:h=0})=>{let[p,m]=(0,l.useState)(h),v=(0,l.useRef)(null),y=(e,t)=>{e.addCommand(t.KeyMod.CtrlCmd|t.KeyCode.Enter,function(){e.getModel();let n=e.getSelection().getStartPosition();e.executeEdits("",[{range:new t.Range(n.lineNumber,n.column,n.lineNumber,n.column),text:"\n"}]),e.setPosition(new t.Position(n.lineNumber+1,n.column));});};function b(e){let t=document.querySelector(".monaco-placeholder");t&&e?(t.style.display="block","off"===x.lineNumbers&&(t.style.left="0px")):t.style.display="none";}(0,l.useEffect)(()=>{b(!n);},[n]);let x=Object.assign({},{wordWrap:"on",wrappingIndent:"indent",minimap:{enabled:!1}},a);return(0,i.jsxs)(g,{ref:v,style:s,className:c,id:"editor-container",children:[(0,i.jsx)(f,{language:e,height:p,value:n,onMount:(e,o)=>{y(e,o),n||b(!0);let r=()=>{var n;let r=e.getOption(o.editor.EditorOption.lineHeight),a=(null===(n=e.getModel())||void 0===n?void 0:n.getLineCount())||1,i=1,d=1/0;"object"==typeof t&&(i=t.minRows||i,d=t.maxRows||d);let s=Math.min(Math.max(a,i),d);m(`${r*s+5}px`);};r(),e.onDidChangeModelContent(r),v.current&&(e.onDidFocusEditorWidget(()=>{var e;null==v||null===(e=v.current)||void 0===e||e.classList.add("focused");}),e.onDidBlurEditorWidget(()=>{var e;null==v||null===(e=v.current)||void 0===e||e.classList.remove("focused");}));},onChange:e=>{null==o||o(e),b(!e);},options:x,theme:u,loading:""}),(0,i.jsx)("div",{className:"monaco-placeholder",children:r})]});};var p=h;},"2caf0dfa":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{fetchSchemaData:function(){return u;},fetchSchemaDetail:function(){return h;},fetchSchemaTree:function(){return c;},knowledgeModelDataState:function(){return l;},setSchemaInfo:function(){return f;}});var o=n("d1751d7c"),r=n("fc5eb767"),a=n("48fdd872"),i=n("5b5ed4a9"),d=n("3834a44f");o._(n("8dec2e97"),t);let s={nodes:[],edges:[]},l=(0,i.proxy)({graphData:void 0,schemaInfo:void 0,fetching:void 0,schemaDetail:void 0,dynamicConfig:void 0,schemaData:s,schemaTreeData:{},relativeProjects:[]}),c=async()=>{let e=Number((0,d.getProjectId)());l.fetching=!0;let t=await (0,a.getSchemaTreeWithProject)({projectId:e});if(t.success&&t.result){let{data:e}=(0,r.addExtraInfoToTree)(t.result,!0);l.schemaTreeData=e,l.relativeProjects=(0,r.getTreeRelativeProjects)(e);}return l.fetching=!1,t;},u=async()=>{let e=Number((0,d.getProjectId)());l.fetching=!0;let t=await (0,a.getSchemaAndRelation)({projectId:e});if(t.success&&t.result){let e=t.result,n=e.entityTypeDTOList||[],o=e.relationTypeDTOList||[],a=(0,r.transformRelationGraphData)({nodes:n.filter(e=>e.name!==r.rootNode.name),edges:o});return l.schemaData=a,l.fetching=!1,a;}return l.schemaData=s,l.fetching=!1,s;},f=e=>{l.schemaInfo=e,e||(l.dynamicConfig=void 0,l.schemaDetail=void 0,l.fetching=void 0);};async function g(e,t){return(0,i.request)("/v1/schemas/getDynamicConfig",{method:"GET",params:{...e},...t||{}});}let h=async e=>{let t;if(!e)return l.fetching=!1;l.fetching=!0;let n=null,{type:o,isSchema:r}=e;if(r){let r="TYPE";switch(o){case"node":case"combo":t=await (0,a.getSchemaEntityProperties)({id:Number(e.id)});break;case"edge":r="RELATION",t=await (0,a.getSchemaRelationProperties)({id:Number(e.id)});}let i=Number(e.id);if(isNaN(i)||(n=await g({type:r,ids:i})),n&&n.success&&n.result?l.dynamicConfig=n.result:l.dynamicConfig=void 0,t.success&&t.result)return l.schemaDetail=t.result,l.fetching=!1,t.result;}l.schemaDetail=void 0,l.dynamicConfig=void 0,l.fetching=!1;};},"2eaf8f67":function(e,t,n){n.d(t,"__esModule",{value:!0}),n.e(t,{default:function(){return B;}});var o=n("777fffbe"),r=n("852bbaa9"),a=n("32b7a2cf"),i=n("fc5eb767"),d=n("5e00c259"),s=n("52fb4bb8"),l=n("cd8b2a5f"),c=n("f02131d0"),u=o._(c),f=n("5b5ed4a9"),g=n("d5b565e0"),h=o._(g),p=n("1e70bad6"),m=o._(p),v=n("4d0e37ae"),y=r._(v),b=n("fd7ca954"),x=n("1b2ef78d"),w=n("8e68a90d"),S=n("5249fab4"),_=n("1ebf6c5c"),T=o._(_),C=n("fc1f4356");let E={wordWrap:"on",minimap:{enabled:!1},lineNumbers:"off",wrappingIndent:"none",renderLineHighlight:"none",overviewRulerBorder:!1,hideCursorInOverviewRuler:!0,glyphMargin:!1,folding:!1,lineDecorationsWidth:0,lineNumbersMinChars:0,selectionHighlight:!1,scrollbar:{vertical:"hidden",horizontal:"hidden"}},I=`
background-color: #fff;
border: 1px solid #e3e4e6;
transition: all 0.3s, height 0s;
border-radius: 6px;
overflow: hidden;
padding: 8px;
&:hover {
  border-color: #597ef7;
}
&.focused {
  border-color: #597ef7;
  box-shadow: 0 0 0 2px rgba(47, 84, 235, 0.2);
  outline: 0;
}
`,M=(0,f.styled)(T.default)`
  ${({$bordered:e})=>e?I:""};
`,N=T.default;N.TextArea=({value:e,onChange:t,options:n,bordered:o=!0,autoSize:r={minRows:2},...i})=>{let d=(0,y.useMemo)(()=>(0,C.merge)(E,n),[n]);return(0,a.jsx)(M,{language:"text",code:e,setCode:t,options:d,autoSize:r,$bordered:o,...i});};var D=n("2b798761"),k=o._(D),R=n("61b1a47a"),O=o._(R),P=n("3834a44f"),L=n("05ecbb5b");async function j(e){return(0,L.request)("/v1/schemas/getSchemaScript",{method:"GET",params:{projectId:e}});}async function K(e){return(0,L.request)("/v1/schemas",{method:"POST",headers:{"Content-Type":"application/json"},data:{data:e}});}let A=e=>{let{onSaveSuccess:t,onSaveFinally:n,onFetchSuccess:o}=e||{},r=(0,P.getProjectId)(),{isFetching:a,refetch:i,data:d=""}=(0,f.useQuery)({queryFn:async()=>{let e=await j(r),t="";return e.success&&e.result&&(null==o||o(e.result),t=e.result),null==n||n(),t;},queryKey:["getSchema",r],enabled:!1,retry:!1}),{run:s,loading:l}=(0,f.useRequest)(K,{manual:!0,onSuccess:()=>{null==t||t();}});return{schemaScript:d,schemaScriptFetching:a,getSchemaScript:i,schemaScriptSaving:l,saveSchemaScript:s};};var F=(0,y.forwardRef)((e,t)=>{let[n,o]=(0,y.useState)(""),{schemaScriptFetching:r,getSchemaScript:i,saveSchemaScript:d,schemaScriptSaving:s}=A({onSaveSuccess:()=>{k.default.success(u.default.get({id:"spg.KnowledgeModel.components.Edit.SaveSuccessfully",dm:"\u4FDD\u5B58\u6210\u529F"}));},onSaveFinally:()=>{w.schemaScriptUiState.saving=!1;},onFetchSuccess:e=>{o(e);}});return(0,y.useEffect)(()=>{i();},[]),(0,y.useImperativeHandle)(t,()=>({onSave:()=>(w.schemaScriptUiState.saving=!0,d(n))})),(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)("div",{className:"schema-editor-title",children:[(r||s)&&(0,a.jsx)(m.default,{indicator:(0,a.jsx)(O.default,{spin:!0,style:{fontSize:11,color:"#fff "}})}),(0,a.jsx)("span",{className:"ml8",children:u.default.get({id:"spg.KnowledgeModel.components.Edit.ModelEditing",dm:"\u6A21\u578B\u7F16\u8F91"})})]}),(0,a.jsx)("div",{className:"schema-editor",children:(0,a.jsx)(N,{language:"plaintext",height:"100%",autoSize:{minRows:10},code:n,setCode:e=>o(e||""),placeholder:"",theme:"vs-dark"})})]});}),G=n("d0031b0d"),U=n("7603c0eb");let B=()=>{let e=(0,f.useNavigate)(),[t]=(0,f.useSearchParams)(),n=t.get("mode"),o=(0,y.useRef)(null),r=(0,s.useKgsHeight)(o),[c,g]=(0,y.useState)(!1),p=(0,y.useRef)(null),[{saving:v}]=(0,s.useProxyState)(w.schemaScriptUiState),_=(0,l.useMemoizedFn)(()=>{e((0,b.urlPathWithQuery)("/knowledgeModeling/knowledgeModel/conceptual",[{item:{mode:n===i.GraphMode.normal?"":n}}],t));}),T=(0,l.useMemoizedFn)(()=>{e((0,b.urlPathWithQuery)("/knowledgeModeling/knowledgeModel/ruleManager",[],t));});return(0,a.jsxs)(U.TopBoxDiv,{children:[(0,a.jsx)(d.Title,{level:"page",title:u.default.get({id:"spg.KnowledgeModeling.KnowledgeModel.KnowledgeModel",dm:"\u77E5\u8BC6\u6A21\u578B"}),titleExtra:(0,a.jsx)("div",{children:c?(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(h.default,{className:"mr8",onClick:()=>{g(!1);},loading:v,children:u.default.get({id:"spg.KnowledgeModeling.KnowledgeModel.Cancel",dm:"\u53D6\u6D88"})}),(0,a.jsx)(h.default,{className:"mr8",type:"primary",onClick:()=>{var e;null===(e=p.current)||void 0===e||e.onSave().then(()=>{g(!1),(0,w.fetchSchemaTree)(),(0,w.fetchSchemaData)();}).finally(()=>{w.schemaScriptUiState.saving=!1;});},loading:v,children:u.default.get({id:"spg.KnowledgeModeling.KnowledgeModel.Save",dm:"\u4FDD\u5B58"})})]}):(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(h.default,{className:"mr8",type:"primary",onClick:()=>{g(!0);},children:u.default.get({id:"spg.KnowledgeModeling.KnowledgeModel.EditSchema",dm:"\u7F16\u8F91schema"})}),(0,a.jsx)(h.default,{className:"mr8",onClick:_,children:u.default.get({id:"spg.KnowledgeModeling.KnowledgeModel.ConceptualModel",dm:"\u6982\u5FF5\u6A21\u578B"})}),(0,a.jsx)(h.default,{onClick:T,children:u.default.get({id:"spg.KnowledgeModeling.KnowledgeModel.RuleManagement",dm:"\u89C4\u5219\u7BA1\u7406"})})]})})}),(0,a.jsx)(U.ContentDiv,{children:c?(0,a.jsx)(F,{ref:p}):(0,a.jsxs)(U.CardWithStyle,{ref:o,children:[r?n===i.GraphMode.tree?(0,a.jsx)(G.KnowledgeModelTreeGraph,{height:r}):(0,a.jsx)(S.KnowledgeModelGraph,{height:r}):(0,a.jsx)(m.default,{style:{height:"100%"},spinning:!0}),(0,a.jsx)(U.KgsSearchStyle,{}),(0,a.jsx)(x.ModeSelect,{})]})})]});};},"3ad5aff1":function(e,t,n){n.d(t,"__esModule",{value:!0}),n.e(t,{DevTools:function(){return W;},ID:function(){return c.ID;},antlr4:function(){return H.default;},default:function(){return $;},registerOptions:function(){return p;}});var o=n("777fffbe"),r=n("852bbaa9"),a=n("cad0a1a4"),i=n("c695ccb5"),d=n("daa55c75"),s=n("f14d90dc"),l=r._(s),c=n("5d87a02b"),u=n("e6b3e60f"),f=n("55dbeba9"),g=o._(f),h=n("46c8e433");let p=e=>{d.MonacoEnvironment.container.get(d.LanguageOptionsRegistry).registerOptions(c.ID,e);},m=()=>d.MonacoEnvironment.container.get(d.LanguageOptionsRegistry).getOptions(c.ID),v=e=>d.MonacoEnvironment.container.get(d.LanguageOptionsRegistry).getOptions(e)||{};var y=this&&this.__awaiter||function(e,t,n,o){return new(n||(n=Promise))(function(r,a){function i(e){try{s(o.next(e));}catch(e){a(e);}}function d(e){try{s(o.throw(e));}catch(e){a(e);}}function s(e){var t;e.done?r(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(i,d);}s((o=o.apply(e,t||[])).next());});};let b={nodes:[],edges:[]};class x{constructor(){this.cacheContainer=new Map,this.queryFunctions=()=>y(this,void 0,void 0,function*(){return this.query("functions",m().queryFunctions,[]);}),this.querySchema=()=>y(this,void 0,void 0,function*(){return this.query("schema",m().querySchema,b);});}static GET_CACHE_INSTANCE(){return this.queryInstance||(this.queryInstance=new x),this.queryInstance;}getContainer(e){let t=this.cacheContainer.get(e);if(!t){let t=new h.LRU({max:5});return this.cacheContainer.set(e,t),t;}return t;}query(e,t,n){return y(this,void 0,void 0,function*(){let o="__default__",r=this.getContainer(e),a=r.get(o);if(a)return a;if(t)try{let e=yield t();return r.set(o,e),e;}catch(e){}return n;});}clear(){this.cacheContainer.forEach(e=>{e.clear();});}}let w=x.GET_CACHE_INSTANCE(),S={querySchema:()=>y(void 0,void 0,void 0,function*(){return yield w.querySchema();}),queryFunctions:()=>y(void 0,void 0,void 0,function*(){return yield w.queryFunctions();})};var _=this&&this.__awaiter||function(e,t,n,o){return new(n||(n=Promise))(function(r,a){function i(e){try{s(o.next(e));}catch(e){a(e);}}function d(e){try{s(o.throw(e));}catch(e){a(e);}}function s(e){var t;e.done?r(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(i,d);}s((o=o.apply(e,t||[])).next());});};class T{constructor(e){this.worker=e;}get triggerCharacters(){return[".",":","|",""];}provideCompletionItems(e,t){return _(this,void 0,void 0,function*(){let n=e.getValue(),o=e.getOffsetAt(t),r=v(e.uri.toString());try{return yield this.worker.getService().doCompletion({code:n,offset:o,lowerCaseKeyword:r.lowerCaseKeyword},S);}catch(e){return{isIncomplete:!1,suggestions:[]};}});}}var C=this&&this.__awaiter||function(e,t,n,o){return new(n||(n=Promise))(function(r,a){function i(e){try{s(o.next(e));}catch(e){a(e);}}function d(e){try{s(o.throw(e));}catch(e){a(e);}}function s(e){var t;e.done?r(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(i,d);}s((o=o.apply(e,t||[])).next());});};class E{constructor(e){this.worker=e;}provideDocumentFormattingEdits(e){return C(this,void 0,void 0,function*(){let t=e.getValue();return[{range:e.getFullModelRange(),text:yield this.worker.getService().doFormat({code:t})}];});}}class I{constructor(e){this.worker=e;}provideDocumentRangeFormattingEdits(e,t){return C(this,void 0,void 0,function*(){let n=e.getValueInRange(t);return[{range:t,text:n}];});}}var M=this&&this.__awaiter||function(e,t,n,o){return new(n||(n=Promise))(function(r,a){function i(e){try{s(o.next(e));}catch(e){a(e);}}function d(e){try{s(o.throw(e));}catch(e){a(e);}}function s(e){var t;e.done?r(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(i,d);}s((o=o.apply(e,t||[])).next());});};class N{constructor(e){this.worker=e;}provideHover(e,t){return M(this,void 0,void 0,function*(){let n=e.getValue(),o=e.getOffsetAt(t);try{return{contents:((yield this.worker.getService().doHover({code:n,offset:o},S))||[]).map(e=>({value:e,isTrusted:!0,supportHtml:!0}))};}catch(e){return{contents:[],range:void 0};}});}}var D=n("d68eb4a8"),k=this&&this.__awaiter||function(e,t,n,o){return new(n||(n=Promise))(function(r,a){function i(e){try{s(o.next(e));}catch(e){a(e);}}function d(e){try{s(o.throw(e));}catch(e){a(e);}}function s(e){var t;e.done?r(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(i,d);}s((o=o.apply(e,t||[])).next());});};class R{constructor(e){this.worker=e;}getLegend(){return D.LEGEND;}provideDocumentSemanticTokens(e,t,n){return k(this,void 0,void 0,function*(){let t=e.getValue(),n=e.getLinesContent();try{let e=(yield this.worker.getService().doCodeLens({code:t,lines:n},S))||[];return{data:new Uint32Array(e)};}catch(e){return{data:new Uint32Array([])};}});}releaseDocumentSemanticTokens(e){}}var O=this&&this.__awaiter||function(e,t,n,o){return new(n||(n=Promise))(function(r,a){function i(e){try{s(o.next(e));}catch(e){a(e);}}function d(e){try{s(o.throw(e));}catch(e){a(e);}}function s(e){var t;e.done?r(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(i,d);}s((o=o.apply(e,t||[])).next());});};class P{constructor(e){this.worker=e;}get signatureHelpTriggerCharacters(){return["(",","];}provideSignatureHelp(e,t){return O(this,void 0,void 0,function*(){let n=e.getValue(),o=e.getOffsetAt(t)+1;try{let e=yield this.worker.getService().doProvideSignature({code:n,offset:o},S);if(e)return{value:e,dispose:()=>{}};return;}catch(e){return;}});}}var L=this&&this.__decorate||function(e,t,n,o){var r,a=arguments.length,i=a<3?t:null===o?o=Object.getOwnPropertyDescriptor(t,n):o;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)i=Reflect.decorate(e,t,n,o);else for(var d=e.length-1;d>=0;d--)(r=e[d])&&(i=(a<3?r(i):a>3?r(t,n,i):r(t,n))||i);return a>3&&i&&Object.defineProperty(t,n,i),i;},j=this&&this.__metadata||function(e,t){if("object"==typeof Reflect&&"function"==typeof Reflect.metadata)return Reflect.metadata(e,t);},K=this&&this.__param||function(e,t){return function(n,o){t(n,o,e);};},A=this&&this.__awaiter||function(e,t,n,o){return new(n||(n=Promise))(function(r,a){function i(e){try{s(o.next(e));}catch(e){a(e);}}function d(e){try{s(o.throw(e));}catch(e){a(e);}}function s(e){var t;e.done?r(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(i,d);}s((o=o.apply(e,t||[])).next());});};let F=class{constructor(e,t){this.listener={},this.editorValidationMap=new Map,this.modelCreate=e=>{let t;let n=e.getLanguageId();if(n!==c.ID)return;let o=e.uri.toString();this.listener[o]=e.onDidChangeContent(()=>{clearTimeout(t);let o=Math.random();this.editorValidationMap.set(e.uri,o),t=window.setTimeout(()=>{this.doValidate(e,e.getValue(),n,o);},300);}),e.onWillDispose(()=>{var e;null===(e=this.listener[o])||void 0===e||e.dispose();});},this.LanguageWorker=e,this.LanguageOptions=t;let n=this.LanguageWorker.getLanguageWorker(c.ID)||"";this.worker=new u.ServiceWorkerClient(n);}init(){this.setupRealConfig();}setupRealConfig(){(0,g.default)(),l.languages.registerCompletionItemProvider(c.ID,new T(this.worker)),l.languages.registerSignatureHelpProvider(c.ID,new P(this.worker)),l.languages.registerDocumentFormattingEditProvider(c.ID,new E(this.worker)),l.languages.registerDocumentRangeFormattingEditProvider(c.ID,new I(this.worker)),l.languages.registerHoverProvider(c.ID,new N(this.worker)),l.languages.registerDocumentSemanticTokensProvider(c.ID,new R(this.worker));}doValidate(e,t,n,o){return A(this,void 0,void 0,function*(){let r=yield this.worker.getService().doValidate({code:t},S);o===this.editorValidationMap.get(e.uri)&&n===e.getLanguageId()&&l.editor.setModelMarkers(e,n,r);});}dispose(){}};F=L([(0,a.singleton)(),K(0,(0,a.inject)(d.LanguageWorkerRegistry)),K(1,(0,a.inject)(d.LanguageOptionsRegistry)),j("design:paramtypes",[d.LanguageWorkerRegistry,d.LanguageOptionsRegistry])],F);var G=this&&this.__decorate||function(e,t,n,o){var r,a=arguments.length,i=a<3?t:null===o?o=Object.getOwnPropertyDescriptor(t,n):o;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)i=Reflect.decorate(e,t,n,o);else for(var d=e.length-1;d>=0;d--)(r=e[d])&&(i=(a<3?r(i):a>3?r(t,n,i):r(t,n))||i);return a>3&&i&&Object.defineProperty(t,n,i),i;},U=this&&this.__metadata||function(e,t){if("object"==typeof Reflect&&"function"==typeof Reflect.metadata)return Reflect.metadata(e,t);},B=this&&this.__param||function(e,t){return function(n,o){t(n,o,e);};};let V=class{constructor(e){this.languageFeature=e;}onInitialize(){l.languages.register({id:c.ID}),l.editor.onDidCreateModel(this.languageFeature.modelCreate),l.languages.onLanguage(c.ID,()=>{this.languageFeature.init();});}beforeCreate(){}afterCreate(){}canHandle(e){return e===c.ID;}dispose(){this.languageFeature.dispose();}};V=G([(0,a.singleton)({contrib:[d.EditorHandlerContribution,d.InitializeContribution]}),B(0,(0,a.inject)(F)),U("design:paramtypes",[F])],V);var q=n("ff47724e"),z=n("58b55345"),W=r._(z),Y=n("b53cfbd1"),H=o._(Y),$=(0,a.Module)(e=>{e(i.WorkerContribution),e(V),e(F),e(q.LightThemeContribution),e(q.DarkThemeContribution);});},"3ba80fd7":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"default",{enumerable:!0,get:function(){return c;}});var o=n("777fffbe"),r=n("32b7a2cf"),a=o._(n("f02131d0")),i=o._(n("a8452b87")),d=o._(n("2b798761"));n("9113bc14");var s=o._(n("02b696d0")),l=n("c3ef8c6b"),c=s.default.memo(e=>{let{text:t="",onCopy:n,children:o,iconStyle:s,showTextInMessage:c,...u}=e,f=c?`${t}  `:"";return(0,r.jsx)(l.CopyToClipboard,{text:String(t),onCopy:(e,t)=>{null==n||n(e,t),d.default.success(a.default.get({id:"spg.components.CopyToClipboard.PrefixstrCopySucceeded",dm:"{prefixStr} \u590D\u5236\u6210\u529F"},{prefixStr:f}));},...u,children:o||(0,r.jsx)(i.default,{style:{fontSize:16,marginLeft:4,...s}})});});},"4c70c3ed":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"getMixedLocaleFieldValue",{enumerable:!0,get:function(){return r;}});let o=n("777fffbe")._(n("f02131d0")).default.getCurrentLocale(),r=(e={},t)=>{let{fileName:n="name",lang:r=o,fileNameZh:a,onlyLocale:i=!1}=t||{},d=a||n+"Zh",s=e["zh-CN"===r?d:n],l=e["zh-CN"===r?n:d];return l||s?l?s?i?s:`${s}(${l})`:l:s:"-";};},"5249fab4":function(e,t,n){n.d(t,"__esModule",{value:!0}),n.e(t,{KnowledgeModelGraph:function(){return C;}});var o=n("777fffbe"),r=n("852bbaa9"),a=n("d1751d7c"),i=n("32b7a2cf"),d=n("fc5eb767"),s=n("52fb4bb8"),l=n("f02131d0"),c=o._(l),u=n("5b5ed4a9"),f=n("1e70bad6"),g=o._(f),h=n("4d0e37ae"),p=r._(h),m=n("3834a44f"),v=n("fd7ca954"),y=n("4a4bb4c2"),b=n("a4474fd3"),x=n("45fba892"),w=o._(x),S=n("9ab824f6"),_=n("1b2ef78d"),T=n("8e68a90d");let C=e=>{let t=(0,u.useNavigate)(),[n]=(0,u.useSearchParams)(),o=(0,m.getProjectId)(),{height:r}=e,[a,l]=(0,p.useState)(null),[{focusNodeId:f}]=(0,s.useProxyState)(T.knowledgeModelUiState),[{schemaData:h}]=(0,s.useProxyState)(T.knowledgeModelDataState),{isLoading:x}=(0,u.useQuery)({queryFn:T.fetchSchemaData,queryKey:["fetchSchemaData",o],staleTime:0});(0,p.useEffect)(()=>()=>{(0,T.setGraphInitValue)();},[]);let C=e=>{(0,T.setFocusNodeId)(e.id),t((0,v.urlPathWithQuery)("/knowledgeModeling/knowledgeModel",[{item:{mode:d.GraphMode.tree,projectId:o}}],n));},E=[{key:"node-config",name:c.default.get({id:"spg.KnowledgeModel.components.Graph.ViewNodeConfiguration",dm:"\u67E5\u770B\u8282\u70B9\u914D\u7F6E"}),enable:({type:e,model:t})=>"node"===e&&!(0,b.isStandardPropertyNode)(t),onClick:e=>e&&C(e.model)}];return(0,p.useEffect)(()=>{if(!a||a.destroyed)return;let e=e=>{let t=a.getElementData(e.target.id).data;(0,T.setSchemaInfo)({type:"node",isSchema:!0,id:String(t.id),entityName:t.name,name:t.name,nameZh:t.nameZh}),(0,T.openSchemaDrawer)();},t=e=>{let t=a.getElementData(e.target.id);if((0,b.isStandardPropertyEdge)(t))return;let n=t.data;(0,T.setSchemaInfo)({type:"edge",isSchema:!0,id:String(n.originId),name:n.type,nameZh:n.typeZh,fromName:n.sourceName,toName:n.targetName}),(0,T.openSchemaDrawer)();},n=()=>{(0,T.closeSchemaDrawer)();};return a.on(S.NodeEvent.CLICK,e),a.on(S.EdgeEvent.CLICK,t),a.on(S.CanvasEvent.CLICK,n),()=>{a.off(S.NodeEvent.CLICK,e),a.off(S.EdgeEvent.CLICK,t),a.off(S.CanvasEvent.CLICK,n);};},[a]),(0,i.jsx)(g.default,{spinning:!!x,children:(0,i.jsxs)(y.Graph,{onInit:e=>l(e),isMergeEdge:!0,isSchema:!0,style:{height:r},schemaData:(0,w.default)(h)||b.EMPTY_DATA,locale:(0,u.getLocale)(),focusNodeId:f,afterFirstRender:e=>{f&&(setTimeout(()=>{e.emit(S.NodeEvent.CLICK,{target:{id:f},targetType:"node"});},16),(0,T.setFocusNodeId)());},options:{autoFit:"center"},children:[(0,i.jsx)(y.Search,{allowShortcutKey:!0,alwaysShowBox:!0,onSelectFinish:(e,t)=>{if(!a||a.destroyed)return;let{id:n}=t,o=a.getElementType(n);a.emit(`${o}:click`,{target:{id:n}});},style:{left:223,top:24}}),(0,i.jsx)(y.Contextmenu,{itemTypes:["node"],items:E}),(0,i.jsx)(y.Toolbar,{style:{bottom:100,left:24},items:[y.ToolbarKey.FitView,y.ToolbarKey.ZoomIn,y.ToolbarKey.ZoomOut,y.ToolbarKey.FullScreen,y.ToolbarKey.ExportImg]}),(0,i.jsx)(y.CategoryLegend,{style:{left:24}}),(0,i.jsx)(_.SchemaDrawer,{})]})});};a._(n("d0031b0d"),t);},"5e00c259":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"Title",{enumerable:!0,get:function(){return d;}});var o=n("32b7a2cf"),r=n("5b5ed4a9");n("4d0e37ae");var a=n("fc1f4356");let i=r.styled.div`
  .header {
    color: var(--dark-shade-85);
    font-weight: var(--font-medium);
    font-size: ${({$level:e})=>"page"===e?"var(--h1-font-size)":"var(--h3-font-size)"};
    line-height: ${({$level:e})=>"page"===e?"28px":"24px"};
  }
`,d=({className:e,style:t,level:n="page",title:d,titleExtra:s,showBack:l=!1,goBackCb:c,children:u})=>{let f=(0,r.useNavigate)();return(0,o.jsxs)(i,{$level:n,className:e,style:t,children:[(0,o.jsxs)("div",{className:"flex-row mb16",children:[(0,o.jsxs)("div",{className:"header",children:[l&&(0,o.jsx)(r.Icon,{className:"icon mr8 pointer",icon:"ant-design:arrow-left-outlined",onClick:()=>(0,a.isFunction)(c)?c():f(-1)}),d]}),(0,o.jsx)("div",{children:s})]}),(0,o.jsx)("div",{children:u})]});};},"7603c0eb":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{BaseBoxDiv:function(){return d;},CardWithStyle:function(){return l;},ContentDiv:function(){return s;},KgsSearchStyle:function(){return c;},TopBoxDiv:function(){return i;}});var o=n("777fffbe"),r=n("5b5ed4a9"),a=o._(n("80411155"));let i=r.styled.div`
  padding: 24px 40px 24px;
  height: var(--content-height);
  display: flex;
  flex-direction: column;

  .schema-editor-title {
    background: #33373e;
    height: 36px;
    color: #ffffffd9;
    padding-left: 24px;
    line-height: 36px;
    font-size: 12px;
    border-top-left-radius: 12px;
    border-top-right-radius: 12px;
  }

  .schema-editor {
    flex: 1;
    overflow: hidden;
    border-bottom-left-radius: 12px;
    border-bottom-right-radius: 12px;
    background: #1e1e1e;
  }
`,d=r.styled.div`
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0px 0px 1px 0px #00000014, 0px 1px 2px 0px #190f0f12,
    0px 2px 4px 0px #0000000d;
  min-height: 300px;
  flex: 1;
  padding: ${e=>e.padding||"0px"};
  overflow: auto;
`,s=r.styled.div`
  height: calc(100vh - 48px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;
`,l=(0,r.styled)(a.default)`
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;

  .ant-card-body {
    overflow: hidden;
    padding: 0;
    flex: 1;
  }
`,c=(0,r.createGlobalStyle)`
  .kg-search-certain-drop {
    .rc-virtual-list{
      .ant-select-item-group {
        position: sticky;
        background: #fff;
        z-index: 10;
        top: 0px;
        color: #ee1010;
      }
    }
  }
`;},"8dec2e97":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"getConceptRelationDetail",{enumerable:!0,get:function(){return r;}});let o={"RpgSys.ArmorClass/abc__isA__RpgSys.ArmorClass/__ROOT__":(0,n("fc5eb767").getConceptRelationDetailMock)()},r=e=>{let{name:t,startEntity:n,endEntity:r}=e,a=`${n.type}/${n.primaryKey}`,i=`${r.type}/${r.primaryKey}`;return o[`${a}__${t}__${i}`]||o["RpgSys.ArmorClass/abc__isA__RpgSys.ArmorClass/__ROOT__"];};},"8e68a90d":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0});var o=n("d1751d7c");o._(n("2caf0dfa"),t),o._(n("c234708e"),t);},a5eab18a:function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{DrawerStyle:function(){return c;},PropertyTable:function(){return g;},PropertyTitle:function(){return h;},RadioGroup:function(){return u;},SiderBtn:function(){return y;},SiderBtnContainer:function(){return b;},SiderContainer:function(){return m;},StyleMenu:function(){return v;},StyledDescriptions:function(){return p;},TabBar:function(){return f;}});var o=n("777fffbe"),r=n("5e00c259"),a=n("5b5ed4a9"),i=o._(n("9ffd6a0f")),d=o._(n("b09e51ac")),s=o._(n("097087ae")),l=o._(n("c7f4f8e9"));let c=(0,a.createGlobalStyle)`
  .akg-components-drawer {
    .ant-drawer-header {
      border-bottom: none;
    }
    .akg-components-drawer-body{
      height: 100%;
    }
  }
`,u=(0,a.styled)(s.default.Group)`
  position: absolute;
  left: 24px;
  top: 24px;
`,f=(0,a.styled)(s.default.Group)`
  position: sticky;
  z-index: 10;
  top: 0;
  padding: 0px 24px 16px !important;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
`,g=(0,a.styled)(l.default)`
  .ant-table-tbody {
    & > tr {
      height: 40px;
      & > td {
        padding: 0;
        padding-left: 16px;
      }
      &.with-children > td:first-child {
        padding-left: 3px;
      }
    }
  }
`,h=(0,a.styled)(r.Title)`
  position: sticky;
  top: 49px;
  z-index: 6;
  padding: 8px 16px 8px 24px;
  background: #fff;

  & > div {
    margin-bottom: 0;
  }
`,p=(0,a.styled)(i.default)`
  .ant-descriptions-item-content {
    color: var(--deep-blue-68);
    &-extra {
      color: var(--deep-blue-35);
    }
  }
`,m=a.styled.div`
  background: #f0f2f500;
  box-shadow: 1px 0px 0 0px #000a1a0f;
  height: 100%;
  position: relative;
`,v=(0,a.styled)(d.default)`
  border-right: none !important;
  background-color: #f0f2f500;
  width: 168px;
  padding: 8px;
  .ant-menu-item {
    color: #000a1ae3;
    border-radius: 6px;
  }
  .ant-menu-item-selected,
  .ant-menu-submenu-selected .ant-menu-item-selected {
    color: #2f54eb;
    background-color: #00000005 !important;
  }
  .ant-menu-item-selected::after {
    display: none;
  }
`,y=a.styled.div`
  width: 80px; /* 原始宽度 */
  height: 14px; /* 原始高度 */
  background-color: #5273a80f; /* 背景颜色 */
  clip-path: polygon(20% 0%, 80% 0%, 100% 100%, 0% 100%); /* 原始裁剪路径 */
  transform-origin: center; /* 以中心点为基准进行旋转 */
  position: absolute;
  right: -40px;
  top: 50%;
  transform: rotate(90deg) translateY(-50%);
`,b=a.styled.div`
  width: 14px;
  height: 84px;
  cursor: pointer;
  filter: drop-shadow(1px 0 0 #e5e6e8);
  position: absolute;
  top: 50%;
  right: -14px;
  z-index: 10;
  transform: translateY(-50%);
  .shape {
    position: absolute;
    top: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: auto 0;
    overflow: hidden;
    cursor: pointer;
    .shapeContent {
      position: relative;
      display: inline-block;
      width: 14px;
      height: 50px;
      background: #f7f8fa;
      border-radius: 6px;
    }
    .shapeContent::before {
      position: absolute;
      right: 0;
      left: -4px;
      z-index: 999;
      width: 18px;
      height: 23px;
      background: inherit;
      content: '';
      bottom: 2px;
      border-radius: 0 0 6px;
      transform: rotate(-40deg);
      transform-origin: 100% 100%;
    }
    .shapeContent::after {
      position: absolute;
      right: 0;
      left: -4px;
      z-index: 999;
      width: 18px;
      height: 23px;
      background: inherit;
      top: 2px;
      border-radius: 0 6px 0 0;
      transform: rotate(40deg);
      transform-origin: 100% 0;
      content: '';
    }
    .icon {
      position: absolute;
      top: 50%;
      left: 0;
      color: #ccc;
      font-size: 10px;
      transform: translateY(-50%);
      stroke: #ccc;
      stroke-width: 10;
    }
  }
`;},bc7b36fe:function(e,t,n){e.exports="";},c234708e:function(e,t,n){n.d(t,"__esModule",{value:!0}),n.e(t,{closeSchemaDrawer:function(){return i;},knowledgeModelUiState:function(){return r;},openSchemaDrawer:function(){return d;},schemaScriptUiState:function(){return c;},setAdvancedPropertyData:function(){return a;},setFocusNodeId:function(){return s;},setGraphInitValue:function(){return l;}});var o=n("5b5ed4a9");let r=(0,o.proxy)({schemaDrawerOpen:!1,advancedPropertyData:void 0}),a=e=>{r.advancedPropertyData=e;},i=()=>{r.schemaDrawerOpen=!1,r.advancedPropertyData=void 0;},d=()=>r.schemaDrawerOpen=!0,s=e=>r.focusNodeId=e,l=e=>{if(e){r.graphInitValue={focusId:String(e)};return;}r.graphInitValue=void 0;},c=(0,o.proxy)({saving:!1});},d0031b0d:function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"KnowledgeModelTreeGraph",{enumerable:!0,get:function(){return b;}});var o=n("777fffbe"),r=n("32b7a2cf"),a=n("fc5eb767"),i=n("52fb4bb8"),d=n("cd8b2a5f"),s=o._(n("f02131d0")),l=n("5b5ed4a9"),c=n("4d0e37ae"),u=n("fc1f4356"),f=n("3834a44f"),g=n("fd7ca954"),h=n("4a4bb4c2"),p=n("9ab824f6"),m=o._(n("1e70bad6"));n("5a70f60d");var v=n("1b2ef78d"),y=n("8e68a90d");let b=e=>{let t=(0,l.useNavigate)(),[n]=(0,l.useSearchParams)(),[o]=(0,f.useProjectId)(),{height:b}=e,[x,w]=(0,c.useState)(null),[{focusNodeId:S}]=(0,i.useProxyState)(y.knowledgeModelUiState),[{schemaTreeData:_}]=(0,i.useProxyState)(y.knowledgeModelDataState),{isLoading:T}=(0,l.useQuery)({queryFn:y.fetchSchemaTree,queryKey:["fetchSchemaTree",o],staleTime:0,refetchOnMount:!1}),C=(0,d.useMemoizedFn)(e=>{e!==a.rootNode.id&&(null==x||x.emit(p.NodeEvent.CLICK,{target:{id:e}}));}),E=(0,d.useMemoizedFn)(e=>{(0,y.setFocusNodeId)(String(e.id)),t((0,g.urlPathWithQuery)("/knowledgeModeling/knowledgeModel",[{item:{mode:"normal",projectId:o}}],n));}),I=[h.ToolbarKey.ZoomIn,h.ToolbarKey.ZoomOut,h.ToolbarKey.FullScreen,h.ToolbarKey.ExportImg],M=[{key:"relation-config",name:s.default.get({id:"spg.KnowledgeModel.components.TreeGraph.ViewRelationshipConfiguration",dm:"\u67E5\u770B\u5173\u7CFB\u914D\u7F6E"}),enable:({type:e,model:t})=>"node"===e&&t.id!==a.rootNode.id,onClick:e=>e&&E(e.model)}];return(0,c.useEffect)(()=>{if(!x||x.destroyed)return;let e=async e=>{var t,n;let o=null===(t=e.target)||void 0===t?void 0:t.id;if(o&&o!==a.rootNode.id&&x){let e=null===(n=x.getElementData(o).data)||void 0===n?void 0:n.schemaType;(0,y.setSchemaInfo)({type:"node",isSchema:!0,id:String(e.id),entityName:e.name,name:e.name,nameZh:e.nameZh}),(0,y.openSchemaDrawer)();}},t=()=>{(0,y.closeSchemaDrawer)();};return x.on(p.NodeEvent.CLICK,e),x.on(p.CanvasEvent.CLICK,t),()=>{x.off(p.NodeEvent.CLICK,e),x.off(p.CanvasEvent.CLICK,t);};},[x]),(0,r.jsx)(m.default,{spinning:!!T||(0,u.isEmpty)(_),children:(0,r.jsxs)(h.TreeGraph,{onInit:e=>w(e),isSchema:!0,data:(0,u.cloneDeep)(_),locale:(0,l.getLocale)(),style:{height:b},begin:[40,64],focusNodeId:S,afterFirstRender:e=>{S&&(setTimeout(()=>{e.emit(p.NodeEvent.CLICK,{target:{id:S},targetType:"node"});}),(0,y.setFocusNodeId)());},children:[(0,r.jsx)(h.Search,{groupConfig:["node"],allowShortcutKey:!0,alwaysShowBox:!0,style:{left:223,top:24},onSelectFinish:C}),(0,r.jsx)(h.Contextmenu,{itemTypes:["node"],items:M}),(0,r.jsx)(h.Toolbar,{items:I,style:{bottom:100,left:24}}),(0,r.jsx)(v.SchemaDrawer,{})]})});};}}]);
