(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["d6a1eff9"],{"07059fbf":function(e,t,l){"use strict";l.d(t,"__esModule",{value:!0}),l.d(t,"default",{enumerable:!0,get:function(){return a;}});var n=l("777fffbe")._(l("f02131d0")),d=l("4d0e37ae");let i={showSizeChanger:!0};function a(e){let[t,l]=(0,d.useState)(e??10),[a,o]=(0,d.useState)(1),r=t*(a-1)+1,s=t*a,u=e=>n.default.get({id:"spg.src.hooks.usePagination.StartinfonoArticleEndinfonoTotalIn",dm:"\u7B2C {startInfoNo} - {endInfoNo} \u6761 / \u5171 {total} \u6761 "},{startInfoNo:r,endInfoNo:s,total:e}),c=(e,t)=>{l(t),o(e);},g=({total:e=0,showTotal:l,...n})=>({pageSize:t,current:a,total:e,getPagination:g,onChange:c,showTotal:l?u:void 0,...i,...n});return{pageSize:t,pageNumber:a,page:a,size:t,limit:t,resetPageNumber:()=>{o(1);},getPagination:g,changePagination:c};}},"1a998156":function(e,t,l){"use strict";l.d(t,"__esModule",{value:!0}),l.e(t,{CONSTRAINT_TYPE:function(){return a;},INFO_COLUMN:function(){return n;},RELATION_CATEGORY:function(){return o;},RELATION_DIRECTION:function(){return i;},RULE_TYPE:function(){return d;},baseSchemePropertyNameMap:function(){return b;},infoColumnMapping:function(){return p;},relationDirectionMapping:function(){return h;},ruleTypeMapping:function(){return m;}});var n,d,i,a,o,r,s,u,c,g,f=l("777fffbe")._(l("f02131d0"));(r=n||(n={}))[r.NAME=0]="NAME",r[r.DESCRIPTION=1]="DESCRIPTION",r[r.MODIFY_TIME=2]="MODIFY_TIME",r[r.KNOWLEDGE_COUNT=3]="KNOWLEDGE_COUNT",r[r.SOURCE=4]="SOURCE",r[r.TARGET=5]="TARGET",r[r.RELATION_DIRECTION=6]="RELATION_DIRECTION";let p={0:f.default.get({id:"spg.src.constants.knowledgeBuild.Name",dm:"\u540D\u79F0"}),1:f.default.get({id:"spg.src.constants.knowledgeBuild.Description",dm:"\u63CF\u8FF0"}),2:f.default.get({id:"spg.src.constants.knowledgeBuild.LastEditTime",dm:"\u6700\u8FD1\u7F16\u8F91\u65F6\u95F4"}),3:f.default.get({id:"spg.src.constants.knowledgeBuild.CumulativeQuantityOfKnowledge",dm:"\u77E5\u8BC6\u7D2F\u79EF\u6570\u91CF"}),4:f.default.get({id:"spg.src.constants.knowledgeBuild.StartingPoint",dm:"\u8D77\u70B9"}),5:f.default.get({id:"spg.src.constants.knowledgeBuild.End",dm:"\u7EC8\u70B9"}),6:f.default.get({id:"spg.src.constants.knowledgeBuild.RelationshipType",dm:"\u5173\u7CFB\u7C7B\u578B"})};(s=d||(d={})).RELATION="RELATION",s.PROPERTY="PROPERTY",s.CONCEPT="CONCEPT";let m={RELATION:{name:"RELATION",nameZh:f.default.get({id:"spg.src.constants.knowledgeBuild.RelationshipRules",dm:"\u5173\u7CFB\u89C4\u5219"})},PROPERTY:{name:"PROPERTY",nameZh:f.default.get({id:"spg.src.constants.knowledgeBuild.AttributeRules",dm:"\u5C5E\u6027\u89C4\u5219"})},CONCEPT:{name:"CONCEPT",nameZh:f.default.get({id:"spg.src.constants.knowledgeBuild.ConceptRules",dm:"\u6982\u5FF5\u89C4\u5219"})}};(u=i||(i={})).TRANSITIVE="TRANSITIVE",u.BOTH="BOTH",u.SINGLE="SINGLE",u.NULL="NULL";let h={TRANSITIVE:f.default.get({id:"spg.src.constants.knowledgeBuild.TransferRelationship",dm:"\u4F20\u9012\u5173\u7CFB"}),BOTH:f.default.get({id:"spg.src.constants.knowledgeBuild.SymmetricRelation",dm:"\u5BF9\u79F0\u5173\u7CFB"}),SINGLE:f.default.get({id:"spg.src.constants.knowledgeBuild.OneWayRelationship",dm:"\u5355\u5411\u5173\u7CFB"}),NULL:f.default.get({id:"spg.src.constants.knowledgeBuild.None",dm:"\u65E0"})};(c=a||(a={})).UNIQUE="UNIQUE",c.REQUIRE="REQUIRE",c.ENUM="ENUM",c.MAXIMUM_LT_OE="MAXIMUM_LT_OE",c.MAXIMUM_LT="MAXIMUM_LT",c.MINIMUM_GT_OE="MINIMUM_GT_OE",c.MINIMUM_GT="MINIMUM_GT",c.REGULAR="REGULAR",c.MULTIVALUE="MULTIVALUE",(g=o||(o={})).BASIC="BASIC_TYPE",g.CONCEPT="CONCEPT_TYPE",g.ENTITY="ENTITY_TYPE",g.EVENT="EVENT_TYPE",g.STANDARD="STANDARD_TYPE",g.ONE_LEVEL="ONE_LEVEL_TYPE";let b={id:"ID",name:f.default.get({id:"spg.src.constants.knowledgeBuild.Name",dm:"\u540D\u79F0"}),description:f.default.get({id:"spg.src.constants.knowledgeBuild.Description",dm:"\u63CF\u8FF0"}),__from_id__:f.default.get({id:"spg.src.constants.knowledgeBuild.StartingPoint",dm:"\u8D77\u70B9"}),__to_id__:f.default.get({id:"spg.src.constants.knowledgeBuild.End",dm:"\u7EC8\u70B9"})};},"1ebf6c5c":function(e,t,l){"use strict";var n=l("852bbaa9")._;l.d(t,"__esModule",{value:!0}),l.e(t,{Editor:function(){return p;},MonacoEditor:function(){return g;},default:function(){return m;}});var d=l("777fffbe"),i=l("852bbaa9"),a=l("32b7a2cf"),o=d._(l("f02131d0")),r=l("5b5ed4a9"),s=i._(l("4d0e37ae")),u=d._(l("1e70bad6"));l("5a70f60d");let c=s.default.lazy(()=>Promise.all([l.ensure("lib_monaco-editor"),l.ensure("7581f1ef")]).then(l.dr(n,l.bind(l,"7581f1ef")))),g=e=>(0,a.jsx)(s.Suspense,{fallback:(0,a.jsx)(u.default,{size:"small"}),children:(0,a.jsx)(c,{...e})}),f=r.styled.div`
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
`,p=({language:e,autoSize:t=!1,code:l,setCode:n,placeholder:d=o.default.get({id:"spg.components.Editor.PleaseEnter",dm:"\u8BF7\u8F93\u5165"}),options:i={},style:r,className:u,theme:c="light",height:p=0})=>{let[m,h]=(0,s.useState)(p),b=(0,s.useRef)(null),v=(e,t)=>{e.addCommand(t.KeyMod.CtrlCmd|t.KeyCode.Enter,function(){e.getModel();let l=e.getSelection().getStartPosition();e.executeEdits("",[{range:new t.Range(l.lineNumber,l.column,l.lineNumber,l.column),text:"\n"}]),e.setPosition(new t.Position(l.lineNumber+1,l.column));});};function E(e){let t=document.querySelector(".monaco-placeholder");t&&e?(t.style.display="block","off"===x.lineNumbers&&(t.style.left="0px")):t.style.display="none";}(0,s.useEffect)(()=>{E(!l);},[l]);let x=Object.assign({},{wordWrap:"on",wrappingIndent:"indent",minimap:{enabled:!1}},i);return(0,a.jsxs)(f,{ref:b,style:r,className:u,id:"editor-container",children:[(0,a.jsx)(g,{language:e,height:m,value:l,onMount:(e,n)=>{v(e,n),l||E(!0);let d=()=>{var l;let d=e.getOption(n.editor.EditorOption.lineHeight),i=(null===(l=e.getModel())||void 0===l?void 0:l.getLineCount())||1,a=1,o=1/0;"object"==typeof t&&(a=t.minRows||a,o=t.maxRows||o);let r=Math.min(Math.max(i,a),o);h(`${d*r+5}px`);};d(),e.onDidChangeModelContent(d),b.current&&(e.onDidFocusEditorWidget(()=>{var e;null==b||null===(e=b.current)||void 0===e||e.classList.add("focused");}),e.onDidBlurEditorWidget(()=>{var e;null==b||null===(e=b.current)||void 0===e||e.classList.remove("focused");}));},onChange:e=>{null==n||n(e),E(!e);},options:x,theme:c,loading:""}),(0,a.jsx)("div",{className:"monaco-placeholder",children:d})]});};var m=p;},"24c8531b":function(e,t,l){l.d(t,"__esModule",{value:!0}),l.e(t,{default:function(){return _;}});var n=l("777fffbe"),d=l("852bbaa9"),i=l("32b7a2cf"),a=l("5b5ed4a9"),o=l("5661e780"),r=n._(o),s=l("4d0e37ae"),u=d._(s),c=l("c5f39a1b"),g=n._(c),f=l("f02131d0"),p=n._(f),m=l("3fe68e88"),h=n._(m),b=l("fd7ca954"),v=l("4c1838f3"),E=n._(v);let{Item:x}=h.default,T=a.styled.div`
  .ant-breadcrumb {
    position: relative;
    left: -8px;
    ol > li {
      .ant-breadcrumb-separator {
        margin: 0;
      }
      .ant-breadcrumb-link {
        .link-breadcrumb-item {
          border-radius: 4px;
          padding: 4px 4px;
          margin-inline: 4px;
          color: var(--deep-blue-47);
          &:hover {
            color: var(--deep-blue-68);
            background-color: var(--hover-color);
          }
        }
      }
      .span-breadcrumb-item {
        color: var(--deep-blue-65);
        padding: 4px var(--padding-mini);
      }
    }
  }
`;var R=l("5e00c259");let N=(0,a.styled)(e=>{let{items:t,wrapperStyle:l,...n}=e,d=(0,a.useSelectedRoutes)(),o=(0,a.useNavigate)(),[r]=(0,a.useSearchParams)(),s=(0,u.useMemo)(()=>{if(t)return t;let e=[];return null==d||d.forEach((t,l)=>{var n,i,a,o,r,s,u,c;(null==t?void 0:null===(n=t.route)||void 0===n?void 0:n.name)&&((0,E.default)(null==t?void 0:null===(i=t.route)||void 0===i?void 0:i.name)?e.push({key:t.pathnameBase,title:p.default.get(null==t?void 0:null===(a=t.route)||void 0===a?void 0:a.name),href:l+1!==d.length?(null==t?void 0:null===(o=t.route)||void 0===o?void 0:o.navPath)||(null==t?void 0:null===(r=t.route)||void 0===r?void 0:r.path):void 0}):e.push({key:t.pathnameBase,title:null==t?void 0:null===(s=t.route)||void 0===s?void 0:s.name,href:l+1!==d.length?(null==t?void 0:null===(u=t.route)||void 0===u?void 0:u.navPath)||(null==t?void 0:null===(c=t.route)||void 0===c?void 0:c.path):void 0}));}),e;},[d]),c=e=>{o((0,b.urlPathWithQuery)(e,[],r));};return(0,i.jsx)(T,{style:{...l},children:(0,i.jsx)(h.default,{...n,children:s.map(e=>(0,i.jsx)(x,{children:e.href?(0,i.jsx)("a",{className:"link-breadcrumb-item",onClick:()=>c(e.href),children:e.title}):(0,i.jsx)("span",{className:"span-breadcrumb-item",children:e.title})},e.key))})});})`
  &.breadcrumb {
    margin-bottom: 10px;
  }
`,I=a.styled.div`
  position: relative;
  padding: 0 var(--padding-lg) var(--padding-lg);
  &.layout-with-breadcrumb {
    padding-top: 12px;
  }
  &.layout-without-breadcrumb {
    padding-top: 24px;
  }
`,_=({children:e,breadcrumb:t=!1,loading:l,title:n,goBack:d,showBack:a,titleProps:o,className:s,...u})=>{if(l)return(0,i.jsx)(r.default,{active:!0});let c=!!t;return(0,i.jsxs)(I,{...u,className:(0,g.default)([{"layout-without-breadcrumb":!c},{"layout-with-breadcrumb":c},s]),children:[!1!==t&&(0,i.jsx)(N,{className:"breadcrumb",...t}),(n||(null==o?void 0:o.title))&&(0,i.jsx)(R.Title,{level:"page",title:n,goBackCb:d,showBack:a,...o}),e]});};},"4c70c3ed":function(e,t,l){"use strict";l.d(t,"__esModule",{value:!0}),l.d(t,"getMixedLocaleFieldValue",{enumerable:!0,get:function(){return d;}});let n=l("777fffbe")._(l("f02131d0")).default.getCurrentLocale(),d=(e={},t)=>{let{fileName:l="name",lang:d=n,fileNameZh:i,onlyLocale:a=!1}=t||{},o=i||l+"Zh",r=e["zh-CN"===d?o:l],s=e["zh-CN"===d?l:o];return s||r?s?r?a?r:`${r}(${s})`:s:r:"-";};},"5e00c259":function(e,t,l){"use strict";l.d(t,"__esModule",{value:!0}),l.d(t,"Title",{enumerable:!0,get:function(){return o;}});var n=l("32b7a2cf"),d=l("5b5ed4a9");l("4d0e37ae");var i=l("fc1f4356");let a=d.styled.div`
  .header {
    color: var(--dark-shade-85);
    font-weight: var(--font-medium);
    font-size: ${({$level:e})=>"page"===e?"var(--h1-font-size)":"var(--h3-font-size)"};
    line-height: ${({$level:e})=>"page"===e?"28px":"24px"};
  }
`,o=({className:e,style:t,level:l="page",title:o,titleExtra:r,showBack:s=!1,goBackCb:u,children:c})=>{let g=(0,d.useNavigate)();return(0,n.jsxs)(a,{$level:l,className:e,style:t,children:[(0,n.jsxs)("div",{className:"flex-row mb16",children:[(0,n.jsxs)("div",{className:"header",children:[s&&(0,n.jsx)(d.Icon,{className:"icon mr8 pointer",icon:"ant-design:arrow-left-outlined",onClick:()=>(0,i.isFunction)(u)?u():g(-1)}),o]}),(0,n.jsx)("div",{children:r})]}),(0,n.jsx)("div",{children:c})]});};},aa7c25b9:function(e,t,l){l.d(t,"__esModule",{value:!0}),l.e(t,{default:function(){return G;}});var n=l("777fffbe"),d=l("852bbaa9"),i=l("32b7a2cf"),a=l("24c8531b"),o=n._(a),r=l("f02131d0"),s=n._(r),u=l("5b5ed4a9"),c=l("673e678e"),g=n._(c),f=l("4d0e37ae"),p=d._(f),m=l("3834a44f"),h=l("4c70c3ed"),b=l("1a998156"),v=l("07059fbf"),E=n._(v),x=l("05ecbb5b");async function T(e,t){return(0,x.request)("/rule/api/getRuleList.json",{method:"GET",params:{...e},...t||{}});}var R=l("d5b565e0"),N=n._(R),I=l("80411155"),_=n._(I),M=l("c7f4f8e9"),w=n._(M),y=l("44cf3c0b"),L=l("1ebf6c5c"),C=l("2ed4c134"),j=n._(C),O=l("2b798761"),k=n._(O),P=l("249809ea"),S=n._(P);let A=(0,u.styled)(j.default)`
  .ant-drawer-header {
    border-bottom: 0;
  }
  .ant-drawer-body {
    padding-top: 0;
    display: flex;
    flex-direction: column;
    height: 100%;
    .dsl-editor-container {
      flex: 1;
      margin-bottom: var(--margin-sm);
      border-radius: var(--border-radius-md);
      border: var(--solid-border);
      overflow: hidden;
    }
    .operate-content {
      width: 100%;
      justify-content: flex-end;
    }
  }
`;async function D(e){return(0,u.request)("/rule/api/updateRule",{method:"POST",headers:{"Content-Type":"application/json"},data:e});}let U=({editing:e=!1,setEditing:t=()=>{},ruleList:l,ruleId:n,onClose:d,prevRule:a,enablePrevRule:o=!1,nextRule:r,enableNextRule:c=!1,btnDisabled:g})=>{var f;let h=(0,m.getProjectId)(),[b,v]=(0,p.useState)((null==l?void 0:null===(f=l.find(e=>e.ruleId===n))||void 0===f?void 0:f.expression)||"");(0,p.useEffect)(()=>{if(n){let e=null==l?void 0:l.find(e=>e.ruleId===n);e&&v(e.expression||"");}},[n,l]);let{run:E,loading:x}=(0,u.useRequest)(D,{manual:!0,formatResult:e=>(e.success&&(k.default.success(s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleDetail.UpdateRuleSucceeded",dm:"\u66F4\u65B0\u89C4\u5219\u6210\u529F"})),d(!0)),e)});return(0,i.jsxs)(A,{width:700,title:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleDetail.DslDetails",dm:"DSL \u8BE6\u60C5"}),extra:e?(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(N.default,{className:"mr8",loading:x,onClick:()=>t(!1),children:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleDetail.Cancel",dm:"\u53D6\u6D88"})}),(0,i.jsx)(N.default,{type:"primary",disabled:!1,loading:x,onClick:()=>{n&&E({projectId:h,ruleId:n,expression:b});},children:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleDetail.Save",dm:"\u4FDD\u5B58"})})]}):(0,i.jsx)(i.Fragment,{}),open:!!n,onClose:()=>{x||(t(!1),v(""),d());},children:[(0,i.jsx)("div",{className:"dsl-editor-container",children:(0,i.jsx)(L.MonacoEditor,{options:{readOnly:!e,theme:"light"},value:b,onChange:e=>{v(e||"");}})}),(0,i.jsxs)(S.default,{className:"operate-content",children:[o&&(0,i.jsx)(N.default,{disabled:g,onClick:a,children:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleDetail.PreviousArticle",dm:"\u4E0A\u4E00\u6761"})}),c&&(0,i.jsx)(N.default,{disabled:g,onClick:r,children:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleDetail.NextArticle",dm:"\u4E0B\u4E00\u6761"})})]})]});},B=(0,u.styled)(_.default)`
  .flex-row {
    align-items: stretch;
  }
  .rule-table {
    flex: 1;
    .ant-btn-link {
      height: 22px;
      line-height: 1;
    }
    .ant-table-selection-column {
      padding: 0;
    }
  }
`,K=({searchKey:e})=>{let t=Number((0,m.getProjectId)()),{getPagination:l}=(0,E.default)(),[n,d]=(0,p.useState)(),[a,o]=(0,p.useState)(!1),{data:{result:r=[]}={},isLoading:c,refetch:g}=(0,u.useQuery)(["getRuleList",{projectId:t}],()=>T({projectId:t}),{enabled:!!t}),f=(0,p.useMemo)(()=>{let t=(0,y.isContainsText)(e);return r.filter(l=>{var n,d,i,a;if(!e||t(null===(n=l.startEntity)||void 0===n?void 0:n.name)||t(null===(d=l.startEntity)||void 0===d?void 0:d.nameZh)||t(null===(i=l.objectType)||void 0===i?void 0:i.name)||t(null===(a=l.objectType)||void 0===a?void 0:a.nameZh)||t(l.name)||t(l.nameZh))return!0;let o=b.ruleTypeMapping[l.ruleType].nameZh;return!!(t(l.ruleType)||t(o));});},[e,r]),v=f.length,x=[{title:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleTable.StartingPoint",dm:"\u8D77\u70B9"}),dataIndex:"startEntity",render:e=>(0,h.getMixedLocaleFieldValue)(e)},{title:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleTable.Relationship",dm:"\u5173\u7CFB"}),dataIndex:"name",render:(e,t)=>(0,h.getMixedLocaleFieldValue)(t)},{title:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleTable.End",dm:"\u7EC8\u70B9"}),dataIndex:"objectType",render:e=>(0,h.getMixedLocaleFieldValue)(e)},{title:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleTable.RuleType",dm:"\u89C4\u5219\u7C7B\u578B"}),dataIndex:"ruleType",render:e=>(0,h.getMixedLocaleFieldValue)(b.ruleTypeMapping[e],{onlyLocale:!0})},{title:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleTable.ModificationTime",dm:"\u4FEE\u6539\u65F6\u95F4"}),dataIndex:"modifiedDate"},{title:"",key:"operate",fixed:"right",render:(e,t)=>(0,i.jsx)(i.Fragment,{children:(0,i.jsx)(N.default,{type:"link",onClick:()=>{d(t.ruleId),o(!1);},children:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleTable.Details",dm:"\u8BE6\u60C5"})})})}],R=e=>{let t=f.findIndex(t=>t.ruleId===e);if(-1!==t&&0!==t){let e=t-1;return{ruleId:f[e].ruleId,ruleListIndex:e};}},I=e=>{let{onChange:t,current:n,pageSize:i}=l({total:v}),a=R(e);a?(d(a.ruleId),(n-1)*i+1>a.ruleListIndex+1&&t(n-1,i)):d(void 0);},_=e=>{let t=f.findIndex(t=>t.ruleId===e);if(-1!==t&&t+1!==v){let e=t+1;return{ruleId:f[e].ruleId,ruleListIndex:e};}},M=e=>{let{onChange:t,current:n,pageSize:i}=l({total:v}),a=_(e);a?(d(a.ruleId),n*i<a.ruleListIndex+1&&t(n+1,i)):d(void 0);};return(0,i.jsx)(B,{children:(0,i.jsxs)("div",{className:"flex-row",children:[(0,i.jsx)(w.default,{className:"rule-table",rowKey:"ruleId",columns:x,dataSource:f,loading:c,rowSelection:{selectedRowKeys:n?[n]:void 0,hideSelectAll:!0,renderCell:()=>(0,i.jsx)(i.Fragment,{}),columnWidth:0},scroll:{x:"max-content"},pagination:l({total:v,showQuickJumper:!0})}),(0,i.jsx)(U,{editing:a,setEditing:o,onClose:e=>{d(void 0),o(!1),e&&g();},ruleId:n,ruleList:f||[],btnDisabled:c,prevRule:()=>I(n),enablePrevRule:!!R(n)&&!a,nextRule:()=>M(n),enableNextRule:!!_(n)&&!a})]})});},Y=(0,u.styled)(o.default)`
  .search-input {
    width: 288px;
  }
`;function G(){let e=(0,u.useNavigate)(),t=(0,m.getProjectId)(),[l,n]=(0,p.useState)(),d=e=>{n(e.target.value);};return(0,i.jsx)(Y,{titleProps:{title:s.default.get({id:"spg.KnowledgeModeling.RuleList.RuleList",dm:"\u89C4\u5219\u5217\u8868"}),showBack:!0,titleExtra:(0,i.jsx)(g.default,{placeholder:s.default.get({id:"spg.KnowledgeModeling.RuleList.PleaseEnterAssociatedEntityConcept",dm:"\u8BF7\u8F93\u5165\u5173\u8054\u5B9E\u4F53/\u6982\u5FF5/\u5173\u7CFB"}),onPressEnter:d,className:"search-input",suffix:(0,i.jsx)(u.Icon,{onClick:d,icon:"ant-design:search-outlined",className:"icon"})})},breadcrumb:!0,goBack:()=>{e(`/knowledgeModeling/knowledgeModel?projectId=${t}`);},children:(0,i.jsx)(K,{searchKey:l})});}},c5f39a1b:function(e,t,l){"use strict";l.d(t,"__esModule",{value:!0}),l.d(t,"default",{enumerable:!0,get:function(){return n;}});var n=l("777fffbe")._(l("85d9e535")).default;}}]);
