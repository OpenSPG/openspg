(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["8dadba10"],{"0b69d691":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{getEntityDetail:function(){return o;},getEnumValues:function(){return i;},getLlmSelect:function(){return a;},getOneHopGraph:function(){return d;},getSampleData:function(){return c;},search:function(){return s;}});var r=n("05ecbb5b");async function o(e,t){return(0,r.request)("/v1/datas/getEntityDetail",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}async function i(e,t){let{name:n}=e;return(0,r.request)(`/v1/datas/getEnumValues/${n}`,{method:"GET",params:{...e},...t||{}});}async function a(e,t){return(0,r.request)("/v1/datas/getLlmSelect",{method:"GET",params:{...e},...t||{}});}async function d(e,t){return(0,r.request)("/v1/datas/getOneHopGraph",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}async function c(e,t){return(0,r.request)("/v1/datas/getSampleData",{method:"GET",params:{...e},...t||{}});}async function s(e,t){return(0,r.request)("/v1/datas/search",{method:"GET",params:{...e},...t||{}});}},"1a998156":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{CONSTRAINT_TYPE:function(){return a;},INFO_COLUMN:function(){return r;},RELATION_CATEGORY:function(){return d;},RELATION_DIRECTION:function(){return i;},RULE_TYPE:function(){return o;},baseSchemePropertyNameMap:function(){return v;},infoColumnMapping:function(){return g;},relationDirectionMapping:function(){return m;},ruleTypeMapping:function(){return h;}});var r,o,i,a,d,c,s,u,l,f,p=n("777fffbe")._(n("f02131d0"));(c=r||(r={}))[c.NAME=0]="NAME",c[c.DESCRIPTION=1]="DESCRIPTION",c[c.MODIFY_TIME=2]="MODIFY_TIME",c[c.KNOWLEDGE_COUNT=3]="KNOWLEDGE_COUNT",c[c.SOURCE=4]="SOURCE",c[c.TARGET=5]="TARGET",c[c.RELATION_DIRECTION=6]="RELATION_DIRECTION";let g={0:p.default.get({id:"spg.src.constants.knowledgeBuild.Name",dm:"\u540D\u79F0"}),1:p.default.get({id:"spg.src.constants.knowledgeBuild.Description",dm:"\u63CF\u8FF0"}),2:p.default.get({id:"spg.src.constants.knowledgeBuild.LastEditTime",dm:"\u6700\u8FD1\u7F16\u8F91\u65F6\u95F4"}),3:p.default.get({id:"spg.src.constants.knowledgeBuild.CumulativeQuantityOfKnowledge",dm:"\u77E5\u8BC6\u7D2F\u79EF\u6570\u91CF"}),4:p.default.get({id:"spg.src.constants.knowledgeBuild.StartingPoint",dm:"\u8D77\u70B9"}),5:p.default.get({id:"spg.src.constants.knowledgeBuild.End",dm:"\u7EC8\u70B9"}),6:p.default.get({id:"spg.src.constants.knowledgeBuild.RelationshipType",dm:"\u5173\u7CFB\u7C7B\u578B"})};(s=o||(o={})).RELATION="RELATION",s.PROPERTY="PROPERTY",s.CONCEPT="CONCEPT";let h={RELATION:{name:"RELATION",nameZh:p.default.get({id:"spg.src.constants.knowledgeBuild.RelationshipRules",dm:"\u5173\u7CFB\u89C4\u5219"})},PROPERTY:{name:"PROPERTY",nameZh:p.default.get({id:"spg.src.constants.knowledgeBuild.AttributeRules",dm:"\u5C5E\u6027\u89C4\u5219"})},CONCEPT:{name:"CONCEPT",nameZh:p.default.get({id:"spg.src.constants.knowledgeBuild.ConceptRules",dm:"\u6982\u5FF5\u89C4\u5219"})}};(u=i||(i={})).TRANSITIVE="TRANSITIVE",u.BOTH="BOTH",u.SINGLE="SINGLE",u.NULL="NULL";let m={TRANSITIVE:p.default.get({id:"spg.src.constants.knowledgeBuild.TransferRelationship",dm:"\u4F20\u9012\u5173\u7CFB"}),BOTH:p.default.get({id:"spg.src.constants.knowledgeBuild.SymmetricRelation",dm:"\u5BF9\u79F0\u5173\u7CFB"}),SINGLE:p.default.get({id:"spg.src.constants.knowledgeBuild.OneWayRelationship",dm:"\u5355\u5411\u5173\u7CFB"}),NULL:p.default.get({id:"spg.src.constants.knowledgeBuild.None",dm:"\u65E0"})};(l=a||(a={})).UNIQUE="UNIQUE",l.REQUIRE="REQUIRE",l.ENUM="ENUM",l.MAXIMUM_LT_OE="MAXIMUM_LT_OE",l.MAXIMUM_LT="MAXIMUM_LT",l.MINIMUM_GT_OE="MINIMUM_GT_OE",l.MINIMUM_GT="MINIMUM_GT",l.REGULAR="REGULAR",l.MULTIVALUE="MULTIVALUE",(f=d||(d={})).BASIC="BASIC_TYPE",f.CONCEPT="CONCEPT_TYPE",f.ENTITY="ENTITY_TYPE",f.EVENT="EVENT_TYPE",f.STANDARD="STANDARD_TYPE",f.ONE_LEVEL="ONE_LEVEL_TYPE";let v={id:"ID",name:p.default.get({id:"spg.src.constants.knowledgeBuild.Name",dm:"\u540D\u79F0"}),description:p.default.get({id:"spg.src.constants.knowledgeBuild.Description",dm:"\u63CF\u8FF0"}),__from_id__:p.default.get({id:"spg.src.constants.knowledgeBuild.StartingPoint",dm:"\u8D77\u70B9"}),__to_id__:p.default.get({id:"spg.src.constants.knowledgeBuild.End",dm:"\u7EC8\u70B9"})};},"2caf0dfa":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{fetchSchemaData:function(){return l;},fetchSchemaDetail:function(){return g;},fetchSchemaTree:function(){return u;},knowledgeModelDataState:function(){return s;},setSchemaInfo:function(){return f;}});var r=n("d1751d7c"),o=n("fc5eb767"),i=n("48fdd872"),a=n("5b5ed4a9"),d=n("3834a44f");r._(n("8dec2e97"),t);let c={nodes:[],edges:[]},s=(0,a.proxy)({graphData:void 0,schemaInfo:void 0,fetching:void 0,schemaDetail:void 0,dynamicConfig:void 0,schemaData:c,schemaTreeData:{},relativeProjects:[]}),u=async()=>{let e=Number((0,d.getProjectId)());s.fetching=!0;let t=await (0,i.getSchemaTreeWithProject)({projectId:e});if(t.success&&t.result){let{data:e}=(0,o.addExtraInfoToTree)(t.result,!0);s.schemaTreeData=e,s.relativeProjects=(0,o.getTreeRelativeProjects)(e);}return s.fetching=!1,t;},l=async()=>{let e=Number((0,d.getProjectId)());s.fetching=!0;let t=await (0,i.getSchemaAndRelation)({projectId:e});if(t.success&&t.result){let e=t.result,n=e.entityTypeDTOList||[],r=e.relationTypeDTOList||[],i=(0,o.transformRelationGraphData)({nodes:n.filter(e=>e.name!==o.rootNode.name),edges:r});return s.schemaData=i,s.fetching=!1,i;}return s.schemaData=c,s.fetching=!1,c;},f=e=>{s.schemaInfo=e,e||(s.dynamicConfig=void 0,s.schemaDetail=void 0,s.fetching=void 0);};async function p(e,t){return(0,a.request)("/v1/schemas/getDynamicConfig",{method:"GET",params:{...e},...t||{}});}let g=async e=>{let t;if(!e)return s.fetching=!1;s.fetching=!0;let n=null,{type:r,isSchema:o}=e;if(o){let o="TYPE";switch(r){case"node":case"combo":t=await (0,i.getSchemaEntityProperties)({id:Number(e.id)});break;case"edge":o="RELATION",t=await (0,i.getSchemaRelationProperties)({id:Number(e.id)});}let a=Number(e.id);if(isNaN(a)||(n=await p({type:o,ids:a})),n&&n.success&&n.result?s.dynamicConfig=n.result:s.dynamicConfig=void 0,t.success&&t.result)return s.schemaDetail=t.result,s.fetching=!1,t.result;}s.schemaDetail=void 0,s.dynamicConfig=void 0,s.fetching=!1;};},"3ad5aff1":function(e,t,n){n.d(t,"__esModule",{value:!0}),n.e(t,{DevTools:function(){return z;},ID:function(){return u.ID;},antlr4:function(){return W.default;},default:function(){return $;},registerOptions:function(){return h;}});var r=n("777fffbe"),o=n("852bbaa9"),i=n("cad0a1a4"),a=n("c695ccb5"),d=n("daa55c75"),c=n("f14d90dc"),s=o._(c),u=n("5d87a02b"),l=n("e6b3e60f"),f=n("55dbeba9"),p=r._(f),g=n("46c8e433");let h=e=>{d.MonacoEnvironment.container.get(d.LanguageOptionsRegistry).registerOptions(u.ID,e);},m=()=>d.MonacoEnvironment.container.get(d.LanguageOptionsRegistry).getOptions(u.ID),v=e=>d.MonacoEnvironment.container.get(d.LanguageOptionsRegistry).getOptions(e)||{};var y=this&&this.__awaiter||function(e,t,n,r){return new(n||(n=Promise))(function(o,i){function a(e){try{c(r.next(e));}catch(e){i(e);}}function d(e){try{c(r.throw(e));}catch(e){i(e);}}function c(e){var t;e.done?o(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(a,d);}c((r=r.apply(e,t||[])).next());});};let b={nodes:[],edges:[]};class x{constructor(){this.cacheContainer=new Map,this.queryFunctions=()=>y(this,void 0,void 0,function*(){return this.query("functions",m().queryFunctions,[]);}),this.querySchema=()=>y(this,void 0,void 0,function*(){return this.query("schema",m().querySchema,b);});}static GET_CACHE_INSTANCE(){return this.queryInstance||(this.queryInstance=new x),this.queryInstance;}getContainer(e){let t=this.cacheContainer.get(e);if(!t){let t=new g.LRU({max:5});return this.cacheContainer.set(e,t),t;}return t;}query(e,t,n){return y(this,void 0,void 0,function*(){let r="__default__",o=this.getContainer(e),i=o.get(r);if(i)return i;if(t)try{let e=yield t();return o.set(r,e),e;}catch(e){}return n;});}clear(){this.cacheContainer.forEach(e=>{e.clear();});}}let _=x.GET_CACHE_INSTANCE(),T={querySchema:()=>y(void 0,void 0,void 0,function*(){return yield _.querySchema();}),queryFunctions:()=>y(void 0,void 0,void 0,function*(){return yield _.queryFunctions();})};var w=this&&this.__awaiter||function(e,t,n,r){return new(n||(n=Promise))(function(o,i){function a(e){try{c(r.next(e));}catch(e){i(e);}}function d(e){try{c(r.throw(e));}catch(e){i(e);}}function c(e){var t;e.done?o(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(a,d);}c((r=r.apply(e,t||[])).next());});};class C{constructor(e){this.worker=e;}get triggerCharacters(){return[".",":","|",""];}provideCompletionItems(e,t){return w(this,void 0,void 0,function*(){let n=e.getValue(),r=e.getOffsetAt(t),o=v(e.uri.toString());try{return yield this.worker.getService().doCompletion({code:n,offset:r,lowerCaseKeyword:o.lowerCaseKeyword},T);}catch(e){return{isIncomplete:!1,suggestions:[]};}});}}var E=this&&this.__awaiter||function(e,t,n,r){return new(n||(n=Promise))(function(o,i){function a(e){try{c(r.next(e));}catch(e){i(e);}}function d(e){try{c(r.throw(e));}catch(e){i(e);}}function c(e){var t;e.done?o(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(a,d);}c((r=r.apply(e,t||[])).next());});};class I{constructor(e){this.worker=e;}provideDocumentFormattingEdits(e){return E(this,void 0,void 0,function*(){let t=e.getValue();return[{range:e.getFullModelRange(),text:yield this.worker.getService().doFormat({code:t})}];});}}class S{constructor(e){this.worker=e;}provideDocumentRangeFormattingEdits(e,t){return E(this,void 0,void 0,function*(){let n=e.getValueInRange(t);return[{range:t,text:n}];});}}var D=this&&this.__awaiter||function(e,t,n,r){return new(n||(n=Promise))(function(o,i){function a(e){try{c(r.next(e));}catch(e){i(e);}}function d(e){try{c(r.throw(e));}catch(e){i(e);}}function c(e){var t;e.done?o(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(a,d);}c((r=r.apply(e,t||[])).next());});};class M{constructor(e){this.worker=e;}provideHover(e,t){return D(this,void 0,void 0,function*(){let n=e.getValue(),r=e.getOffsetAt(t);try{return{contents:((yield this.worker.getService().doHover({code:n,offset:r},T))||[]).map(e=>({value:e,isTrusted:!0,supportHtml:!0}))};}catch(e){return{contents:[],range:void 0};}});}}var R=n("d68eb4a8"),N=this&&this.__awaiter||function(e,t,n,r){return new(n||(n=Promise))(function(o,i){function a(e){try{c(r.next(e));}catch(e){i(e);}}function d(e){try{c(r.throw(e));}catch(e){i(e);}}function c(e){var t;e.done?o(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(a,d);}c((r=r.apply(e,t||[])).next());});};class k{constructor(e){this.worker=e;}getLegend(){return R.LEGEND;}provideDocumentSemanticTokens(e,t,n){return N(this,void 0,void 0,function*(){let t=e.getValue(),n=e.getLinesContent();try{let e=(yield this.worker.getService().doCodeLens({code:t,lines:n},T))||[];return{data:new Uint32Array(e)};}catch(e){return{data:new Uint32Array([])};}});}releaseDocumentSemanticTokens(e){}}var O=this&&this.__awaiter||function(e,t,n,r){return new(n||(n=Promise))(function(o,i){function a(e){try{c(r.next(e));}catch(e){i(e);}}function d(e){try{c(r.throw(e));}catch(e){i(e);}}function c(e){var t;e.done?o(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(a,d);}c((r=r.apply(e,t||[])).next());});};class L{constructor(e){this.worker=e;}get signatureHelpTriggerCharacters(){return["(",","];}provideSignatureHelp(e,t){return O(this,void 0,void 0,function*(){let n=e.getValue(),r=e.getOffsetAt(t)+1;try{let e=yield this.worker.getService().doProvideSignature({code:n,offset:r},T);if(e)return{value:e,dispose:()=>{}};return;}catch(e){return;}});}}var P=this&&this.__decorate||function(e,t,n,r){var o,i=arguments.length,a=i<3?t:null===r?r=Object.getOwnPropertyDescriptor(t,n):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)a=Reflect.decorate(e,t,n,r);else for(var d=e.length-1;d>=0;d--)(o=e[d])&&(a=(i<3?o(a):i>3?o(t,n,a):o(t,n))||a);return i>3&&a&&Object.defineProperty(t,n,a),a;},A=this&&this.__metadata||function(e,t){if("object"==typeof Reflect&&"function"==typeof Reflect.metadata)return Reflect.metadata(e,t);},j=this&&this.__param||function(e,t){return function(n,r){t(n,r,e);};},G=this&&this.__awaiter||function(e,t,n,r){return new(n||(n=Promise))(function(o,i){function a(e){try{c(r.next(e));}catch(e){i(e);}}function d(e){try{c(r.throw(e));}catch(e){i(e);}}function c(e){var t;e.done?o(e.value):((t=e.value)instanceof n?t:new n(function(e){e(t);})).then(a,d);}c((r=r.apply(e,t||[])).next());});};let U=class{constructor(e,t){this.listener={},this.editorValidationMap=new Map,this.modelCreate=e=>{let t;let n=e.getLanguageId();if(n!==u.ID)return;let r=e.uri.toString();this.listener[r]=e.onDidChangeContent(()=>{clearTimeout(t);let r=Math.random();this.editorValidationMap.set(e.uri,r),t=window.setTimeout(()=>{this.doValidate(e,e.getValue(),n,r);},300);}),e.onWillDispose(()=>{var e;null===(e=this.listener[r])||void 0===e||e.dispose();});},this.LanguageWorker=e,this.LanguageOptions=t;let n=this.LanguageWorker.getLanguageWorker(u.ID)||"";this.worker=new l.ServiceWorkerClient(n);}init(){this.setupRealConfig();}setupRealConfig(){(0,p.default)(),s.languages.registerCompletionItemProvider(u.ID,new C(this.worker)),s.languages.registerSignatureHelpProvider(u.ID,new L(this.worker)),s.languages.registerDocumentFormattingEditProvider(u.ID,new I(this.worker)),s.languages.registerDocumentRangeFormattingEditProvider(u.ID,new S(this.worker)),s.languages.registerHoverProvider(u.ID,new M(this.worker)),s.languages.registerDocumentSemanticTokensProvider(u.ID,new k(this.worker));}doValidate(e,t,n,r){return G(this,void 0,void 0,function*(){let o=yield this.worker.getService().doValidate({code:t},T);r===this.editorValidationMap.get(e.uri)&&n===e.getLanguageId()&&s.editor.setModelMarkers(e,n,o);});}dispose(){}};U=P([(0,i.singleton)(),j(0,(0,i.inject)(d.LanguageWorkerRegistry)),j(1,(0,i.inject)(d.LanguageOptionsRegistry)),A("design:paramtypes",[d.LanguageWorkerRegistry,d.LanguageOptionsRegistry])],U);var B=this&&this.__decorate||function(e,t,n,r){var o,i=arguments.length,a=i<3?t:null===r?r=Object.getOwnPropertyDescriptor(t,n):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)a=Reflect.decorate(e,t,n,r);else for(var d=e.length-1;d>=0;d--)(o=e[d])&&(a=(i<3?o(a):i>3?o(t,n,a):o(t,n))||a);return i>3&&a&&Object.defineProperty(t,n,a),a;},K=this&&this.__metadata||function(e,t){if("object"==typeof Reflect&&"function"==typeof Reflect.metadata)return Reflect.metadata(e,t);},V=this&&this.__param||function(e,t){return function(n,r){t(n,r,e);};};let F=class{constructor(e){this.languageFeature=e;}onInitialize(){s.languages.register({id:u.ID}),s.editor.onDidCreateModel(this.languageFeature.modelCreate),s.languages.onLanguage(u.ID,()=>{this.languageFeature.init();});}beforeCreate(){}afterCreate(){}canHandle(e){return e===u.ID;}dispose(){this.languageFeature.dispose();}};F=B([(0,i.singleton)({contrib:[d.EditorHandlerContribution,d.InitializeContribution]}),V(0,(0,i.inject)(U)),K("design:paramtypes",[U])],F);var q=n("ff47724e"),Y=n("58b55345"),z=o._(Y),H=n("b53cfbd1"),W=r._(H),$=(0,i.Module)(e=>{e(a.WorkerContribution),e(F),e(U),e(q.LightThemeContribution),e(q.DarkThemeContribution);});},"3ba80fd7":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"default",{enumerable:!0,get:function(){return u;}});var r=n("777fffbe"),o=n("32b7a2cf"),i=r._(n("f02131d0")),a=r._(n("a8452b87")),d=r._(n("2b798761"));n("9113bc14");var c=r._(n("02b696d0")),s=n("c3ef8c6b"),u=c.default.memo(e=>{let{text:t="",onCopy:n,children:r,iconStyle:c,showTextInMessage:u,...l}=e,f=u?`${t}  `:"";return(0,o.jsx)(s.CopyToClipboard,{text:String(t),onCopy:(e,t)=>{null==n||n(e,t),d.default.success(i.default.get({id:"spg.components.CopyToClipboard.PrefixstrCopySucceeded",dm:"{prefixStr} \u590D\u5236\u6210\u529F"},{prefixStr:f}));},...l,children:r||(0,o.jsx)(a.default,{style:{fontSize:16,marginLeft:4,...c}})});});},"4c70c3ed":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"getMixedLocaleFieldValue",{enumerable:!0,get:function(){return o;}});let r=n("777fffbe")._(n("f02131d0")).default.getCurrentLocale(),o=(e={},t)=>{let{fileName:n="name",lang:o=r,fileNameZh:i,onlyLocale:a=!1}=t||{},d=i||n+"Zh",c=e["zh-CN"===o?d:n],s=e["zh-CN"===o?n:d];return s||c?s?c?a?c:`${c}(${s})`:s:c:"-";};},"5e00c259":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"Title",{enumerable:!0,get:function(){return d;}});var r=n("32b7a2cf"),o=n("5b5ed4a9");n("4d0e37ae");var i=n("fc1f4356");let a=o.styled.div`
  .header {
    color: var(--dark-shade-85);
    font-weight: var(--font-medium);
    font-size: ${({$level:e})=>"page"===e?"var(--h1-font-size)":"var(--h3-font-size)"};
    line-height: ${({$level:e})=>"page"===e?"28px":"24px"};
  }
`,d=({className:e,style:t,level:n="page",title:d,titleExtra:c,showBack:s=!1,goBackCb:u,children:l})=>{let f=(0,o.useNavigate)();return(0,r.jsxs)(a,{$level:n,className:e,style:t,children:[(0,r.jsxs)("div",{className:"flex-row mb16",children:[(0,r.jsxs)("div",{className:"header",children:[s&&(0,r.jsx)(o.Icon,{className:"icon mr8 pointer",icon:"ant-design:arrow-left-outlined",onClick:()=>(0,i.isFunction)(u)?u():f(-1)}),d]}),(0,r.jsx)("div",{children:c})]}),(0,r.jsx)("div",{children:l})]});};},"7603c0eb":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{BaseBoxDiv:function(){return d;},CardWithStyle:function(){return s;},ContentDiv:function(){return c;},KgsSearchStyle:function(){return u;},TopBoxDiv:function(){return a;}});var r=n("777fffbe"),o=n("5b5ed4a9"),i=r._(n("80411155"));let a=o.styled.div`
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
`,d=o.styled.div`
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0px 0px 1px 0px #00000014, 0px 1px 2px 0px #190f0f12,
    0px 2px 4px 0px #0000000d;
  min-height: 300px;
  flex: 1;
  padding: ${e=>e.padding||"0px"};
  overflow: auto;
`,c=o.styled.div`
  height: calc(100vh - 48px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;
`,s=(0,o.styled)(i.default)`
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;

  .ant-card-body {
    overflow: hidden;
    padding: 0;
    flex: 1;
  }
`,u=(0,o.createGlobalStyle)`
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
`;},"8dec2e97":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"getConceptRelationDetail",{enumerable:!0,get:function(){return o;}});let r={"RpgSys.ArmorClass/abc__isA__RpgSys.ArmorClass/__ROOT__":(0,n("fc5eb767").getConceptRelationDetailMock)()},o=e=>{let{name:t,startEntity:n,endEntity:o}=e,i=`${n.type}/${n.primaryKey}`,a=`${o.type}/${o.primaryKey}`;return r[`${i}__${t}__${a}`]||r["RpgSys.ArmorClass/abc__isA__RpgSys.ArmorClass/__ROOT__"];};},"8e68a90d":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0});var r=n("d1751d7c");r._(n("2caf0dfa"),t),r._(n("c234708e"),t);},a5eab18a:function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{DrawerStyle:function(){return u;},PropertyTable:function(){return p;},PropertyTitle:function(){return g;},RadioGroup:function(){return l;},SiderBtn:function(){return y;},SiderBtnContainer:function(){return b;},SiderContainer:function(){return m;},StyleMenu:function(){return v;},StyledDescriptions:function(){return h;},TabBar:function(){return f;}});var r=n("777fffbe"),o=n("5e00c259"),i=n("5b5ed4a9"),a=r._(n("9ffd6a0f")),d=r._(n("b09e51ac")),c=r._(n("097087ae")),s=r._(n("c7f4f8e9"));let u=(0,i.createGlobalStyle)`
  .akg-components-drawer {
    .ant-drawer-header {
      border-bottom: none;
    }
    .akg-components-drawer-body{
      height: 100%;
    }
  }
`,l=(0,i.styled)(c.default.Group)`
  position: absolute;
  left: 24px;
  top: 24px;
`,f=(0,i.styled)(c.default.Group)`
  position: sticky;
  z-index: 10;
  top: 0;
  padding: 0px 24px 16px !important;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
`,p=(0,i.styled)(s.default)`
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
`,g=(0,i.styled)(o.Title)`
  position: sticky;
  top: 49px;
  z-index: 6;
  padding: 8px 16px 8px 24px;
  background: #fff;

  & > div {
    margin-bottom: 0;
  }
`,h=(0,i.styled)(a.default)`
  .ant-descriptions-item-content {
    color: var(--deep-blue-68);
    &-extra {
      color: var(--deep-blue-35);
    }
  }
`,m=i.styled.div`
  background: #f0f2f500;
  box-shadow: 1px 0px 0 0px #000a1a0f;
  height: 100%;
  position: relative;
`,v=(0,i.styled)(d.default)`
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
`,y=i.styled.div`
  width: 80px; /* 原始宽度 */
  height: 14px; /* 原始高度 */
  background-color: #5273a80f; /* 背景颜色 */
  clip-path: polygon(20% 0%, 80% 0%, 100% 100%, 0% 100%); /* 原始裁剪路径 */
  transform-origin: center; /* 以中心点为基准进行旋转 */
  position: absolute;
  right: -40px;
  top: 50%;
  transform: rotate(90deg) translateY(-50%);
`,b=i.styled.div`
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
`;},b3b34ef7:function(e,t,n){n.d(t,"__esModule",{value:!0}),n.e(t,{ConceptualModelTreeGraph:function(){return E;}});var r=n("777fffbe"),o=n("852bbaa9"),i=n("32b7a2cf"),a=n("fc5eb767"),d=n("52fb4bb8"),c=n("cd8b2a5f"),s=n("f02131d0"),u=r._(s),l=n("5b5ed4a9"),f=n("2b798761");r._(f);var p=n("4d0e37ae"),g=o._(p),h=n("fc1f4356"),m=n("3834a44f");n("9f12178c");var v=n("8e68a90d"),y=n("8a175809"),b=n("ec5db6c0"),x=n("4a4bb4c2"),_=n("9ab824f6"),T=n("1e70bad6"),w=r._(T);n("5a70f60d");var C=n("1b2ef78d");let E=e=>{let{height:t,isLoading:n=!1}=e,[r,o]=(0,g.useState)([]),[s,f]=(0,g.useState)(null),[{conceptTreeData:p,fetching:T}]=(0,d.useProxyState)(y.conceptModelDataState),[E]=(0,m.useProjectId)(),{isLoading:I}=(0,l.useQuery)({queryFn:y.fetchConceptTree,queryKey:["fetchConceptTree",E],staleTime:0,refetchOnMount:!1}),S=(0,c.useMemoizedFn)((e,t)=>{if(!s||s.destroyed)return;let{id:n}=t;n!==a.virtualConcept.id&&setTimeout(()=>{s.emit("node:click",{target:{id:n}});},0);});(0,g.useEffect)(()=>{if(!s)return;s.on(b.TreeEvent.COLLAPSE_EXPAND,e=>{let{collapsed:t,id:n}=e;o(e=>t?[...e.filter(e=>n!==e)]:[...e,n]);});let e=e=>{var t,n,r;if((0,h.isEmpty)(e))return[];let i=(null===(t=e.conceptRes)||void 0===t?void 0:t.primaryKey)||(null===(n=e.conceptRes)||void 0===n?void 0:n.name);return(null===(r=e.conceptRes)||void 0===r?void 0:r.metaConcept)&&(i=a.rootKey),(0,y.expandConcept)({metaType:e.conceptRes.label,primaryKey:i},e.id),o(t=>[...t,e.id]),[];};s.on(b.TreeEvent.ADD_CHILD,t=>{e(s.getNodeData(t.id));});},[s]),(0,g.useEffect)(()=>{if(!s||s.destroyed)return;let e=async e=>{var t;let n=null===(t=e.target)||void 0===t?void 0:t.id;if(n&&n!==a.virtualConcept.id&&s){let e=s.getElementData(n).data;e.metaConcept?(0,v.setSchemaInfo)({type:"node",isSchema:!0,id:e.originId||"",entityName:e.label||"",name:e.label||"",nameZh:e.nameZh||""}):(0,v.setSchemaInfo)({type:"node",isSchema:!1,metaType:e.label||"",primaryKey:e.primaryKey||e.name||"",name:e.name||e.primaryKey||""}),(0,v.openSchemaDrawer)();}},t=()=>{(0,v.closeSchemaDrawer)();};return s.on(_.NodeEvent.CLICK,e),s.on(_.CanvasEvent.CLICK,t),()=>{s.off(_.NodeEvent.CLICK,e),s.off(_.CanvasEvent.CLICK,t);};},[s]);let D=[x.ToolbarKey.ZoomIn,x.ToolbarKey.ZoomOut,x.ToolbarKey.FullScreen,x.ToolbarKey.ExportImg];return(0,i.jsx)(w.default,{spinning:!!I||!!T||n,children:(0,i.jsxs)(x.TreeGraph,{onInit:e=>f(e),isSchema:!0,data:p,style:{height:t},locale:(0,l.getLocale)(),begin:[40,64],expandedIds:r,children:[(0,i.jsx)(x.Search,{groupConfig:["node"],style:{left:24,top:24},placeholder:u.default.get({id:"spg.ConceptualModel.components.TreeGraph.SearchForConceptsInThe",dm:"\u641C\u7D22\u753B\u5E03\u4E2D\u7684\u6982\u5FF5"}),nodeGroupTitle:u.default.get({id:"spg.ConceptualModel.components.TreeGraph.Concept",dm:"\u6982\u5FF5"}),alwaysShowBox:!0,allowShortcutKey:!0,onSelectFinish:S}),(0,i.jsx)(x.Toolbar,{style:{bottom:100,left:24},items:D}),(0,i.jsx)(C.SchemaDrawer,{})]})});};},ba6f46b8:function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{CardWithStyle:function(){return c;},ContentDiv:function(){return d;},TopBoxDiv:function(){return a;}});var r=n("777fffbe"),o=n("5b5ed4a9"),i=r._(n("80411155"));let a=o.styled.div`
  padding: 12px 40px 24px;
  height: var(--content-height);
  display: flex;
  flex-direction: column;
`,d=o.styled.div`
  height: calc(100vh - 48px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;
`,c=(0,o.styled)(i.default)`
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;

  .ant-card-body {
    overflow: hidden;
    padding: 0;
    flex: 1;
  }
`;},bc7b36fe:function(e,t,n){e.exports="";},c234708e:function(e,t,n){n.d(t,"__esModule",{value:!0}),n.e(t,{closeSchemaDrawer:function(){return a;},knowledgeModelUiState:function(){return o;},openSchemaDrawer:function(){return d;},schemaScriptUiState:function(){return u;},setAdvancedPropertyData:function(){return i;},setFocusNodeId:function(){return c;},setGraphInitValue:function(){return s;}});var r=n("5b5ed4a9");let o=(0,r.proxy)({schemaDrawerOpen:!1,advancedPropertyData:void 0}),i=e=>{o.advancedPropertyData=e;},a=()=>{o.schemaDrawerOpen=!1,o.advancedPropertyData=void 0;},d=()=>o.schemaDrawerOpen=!0,c=e=>o.focusNodeId=e,s=e=>{if(e){o.graphInitValue={focusId:String(e)};return;}o.graphInitValue=void 0;},u=(0,r.proxy)({saving:!1});},c6edf6dc:function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"default",{enumerable:!0,get:function(){return b;}});var r=n("777fffbe"),o=n("32b7a2cf"),i=n("fc5eb767"),a=n("5e00c259"),d=n("52fb4bb8"),c=n("cd8b2a5f"),s=r._(n("f02131d0")),u=n("5b5ed4a9"),l=r._(n("3fe68e88")),f=n("4d0e37ae"),p=n("3834a44f"),g=n("fd7ca954"),h=n("7603c0eb"),m=n("8e68a90d"),v=n("b3b34ef7"),y=n("ba6f46b8");function b(){let e=(0,u.useNavigate)(),[t]=(0,u.useSearchParams)(),[n]=(0,p.useProjectId)(),{isLoading:r}=(0,u.useQuery)({queryFn:m.fetchSchemaData,queryKey:["fetchSchemaData",n],staleTime:0,refetchOnMount:!1}),b=t.get("mode"),x=(0,f.useRef)(null),_=(0,d.useKgsHeight)(x),T=(0,c.useMemoizedFn)(()=>{e((0,g.urlPathWithQuery)("/knowledgeModeling/knowledgeModel",[{item:{mode:b===i.GraphMode.normal?"":b}}],t));});return(0,o.jsxs)(y.TopBoxDiv,{children:[(0,o.jsxs)(l.default,{style:{marginBottom:12},children:[(0,o.jsx)(l.default.Item,{className:"pointer",onClick:T,children:s.default.get({id:"spg.KnowledgeModeling.ConceptualModel.KnowledgeModel",dm:"\u77E5\u8BC6\u6A21\u578B"})}),(0,o.jsx)(l.default.Item,{children:s.default.get({id:"spg.KnowledgeModeling.ConceptualModel.ConceptualModel",dm:"\u6982\u5FF5\u6A21\u578B"})})]}),(0,o.jsx)(a.Title,{level:"page",title:s.default.get({id:"spg.KnowledgeModeling.ConceptualModel.ConceptualModel",dm:"\u6982\u5FF5\u6A21\u578B"}),showBack:!0,goBackCb:T}),(0,o.jsx)(y.ContentDiv,{children:(0,o.jsxs)(y.CardWithStyle,{ref:x,children:[(0,o.jsx)(v.ConceptualModelTreeGraph,{height:_}),(0,o.jsx)(h.KgsSearchStyle,{isLoading:r})]})})]});}}}]);
