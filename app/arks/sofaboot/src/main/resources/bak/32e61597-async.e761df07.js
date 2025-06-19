(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["32e61597"],{"5424cd43":function(e,o,n){n.d(o,"__esModule",{value:!0}),n.e(o,{default:function(){return k;}});var t=n("777fffbe"),i=n("852bbaa9"),a=n("32b7a2cf"),d=n("5b5ed4a9"),r=n("4d0e37ae"),l=i._(r),s=n("f02131d0"),p=t._(s),g=n("fd7ca954"),c=n("483403ba"),u=n("05ecbb5b"),f=n("7f371f1e"),h=t._(f);let m=[{path:"",redirect:"/home"},{path:"/",redirect:"/home"},{name:{id:"spg.config.routes.HomePage",dm:"\u9996\u9875"},path:"/home",wrappers:["./Login/AuthorityWrapper"],routes:[{path:"",component:"./Home"},{path:"/home/create",component:"./Home/CreateNew"},{path:"/home/globalConfig",component:"./Home/GlobalConfig"}]},{name:{dm:"\u767B\u5F55"},path:"/login",component:"./Login",layout:!1},{name:{id:"spg.Home.components.ProjectCard.KnowledgeBuilding",dm:"\u77E5\u8BC6\u5E93\u7BA1\u7406"},path:"/knowledgeModeling",navPath:"/knowledgeModeling/knowledgeTask",component:"./KnowledgeModeling/Layout",routes:[{path:"",redirect:"/knowledgeModeling/knowledgeTask"},{path:"/knowledgeModeling/knowledgeTask",hideChildrenInMenu:!0,name:{id:"spg.Home.components.ProjectCard.KnowledgeBuilding",dm:"\u77E5\u8BC6\u5E93\u7BA1\u7406"},icon:"ant-design:container-outlined",routes:[{path:"",component:"./KnowledgeModeling/KnowledgeTask"},{path:"/knowledgeModeling/knowledgeTask/edit",component:"./KnowledgeModeling/EditTask"},{path:"/knowledgeModeling/knowledgeTask/details",component:"./KnowledgeModeling/KnowledgeDetails"}]},{path:"/knowledgeModeling/knowledgeModel",hideChildrenInMenu:!0,name:{id:"spg.KnowledgeModeling.KnowledgeModel.KnowledgeModel",dm:"\u77E5\u8BC6\u6A21\u578B"},icon:"local:model",routes:[{path:"",component:"./KnowledgeModeling/KnowledgeModel"},{name:{id:"spg.config.routes.ConceptualModel",dm:"\u6982\u5FF5\u6A21\u578B"},path:"/knowledgeModeling/knowledgeModel/conceptual",component:"./KnowledgeModeling/ConceptualModel"},{name:{id:"spg.config.routes.RuleList",dm:"\u89C4\u5219\u5217\u8868"},path:"/knowledgeModeling/knowledgeModel/ruleManager",component:"./KnowledgeModeling/RuleList"}]},{path:"/knowledgeModeling/knowledgeExploration",hideChildrenInMenu:!0,name:{id:"spg.KnowledgeModeling.KnowledgeExploration.KnowledgeExploration",dm:"\u77E5\u8BC6\u63A2\u67E5"},icon:"ant-design:deployment-unit-outlined",component:"./KnowledgeModeling/KnowledgeExploration",routes:[{path:"",redirect:"/knowledgeModeling/knowledgeExploration/manage",keepQuery:!0},{path:"/knowledgeModeling/knowledgeExploration/manage",component:"./KnowledgeModeling/KnowledgeExploration/Manage"}]}]},{name:{id:"spg.config.routes.KnowledgeBaseQA",dm:"\u63A8\u7406\u95EE\u7B54"},path:"/analysisReasoning",component:"./AnalysisReasoning"},{name:{id:"spg.Home.components.ProjectCard.ProjectConfiguration",dm:"\u77E5\u8BC6\u5E93\u914D\u7F6E"},path:"/projectConfig",navPath:"/projectConfig/config",component:"./KnowledgeModeling/Layout",routes:[{path:"",redirect:"/projectConfig/config",keepQuery:!0},{path:"/projectConfig/config",keepQuery:!0,hideChildrenInMenu:!1,name:{id:"spg.routes.name.Configuration",dm:"\u914D\u7F6E"},icon:"ant-design:setting-outlined",routes:[{path:"",redirect:"/projectConfig/config/project",keepQuery:!0},{path:"/projectConfig/config/project",name:{id:"spg.ProjectConfig.Config.Project.KnowledgeBaseConfiguration",dm:"\u77E5\u8BC6\u5E93\u914D\u7F6E"},component:"./ProjectConfig/Config/Project"},{path:"/projectConfig/config/modal",name:{id:"spg.ProjectConfig.Config.Modal.ModelConfiguration",dm:"\u6A21\u578B\u914D\u7F6E"},component:"./ProjectConfig/Config/Modal"}]},{path:"/projectConfig/permission",keepQuery:!0,hideChildrenInMenu:!0,name:{id:"spg.ProjectConfig.Permission.Permissions",dm:"\u6743\u9650"},icon:"ant-design:team-outlined",component:"./ProjectConfig/Permission"}]},{path:"/noProjectPermission",keepQuery:!0,component:"./NoProjectPermission"}];var x=n("a5eab18a");let w=e=>e.map(e=>{if(!e.path)return null;let o=e.routes&&Array.isArray(e.routes)&&!e.hideChildrenInMenu?w(e.routes):void 0;return{label:(0,a.jsx)(c.EllipsisTextTip,{text:p.default.get(e.name),tooltipProps:{placement:"right"}}),...e.icon&&{icon:(0,a.jsx)(u.Icon,{icon:e.icon,style:{width:16,height:16,lineHeight:"16px"}})},key:e.path,children:o};}).filter(Boolean),b=l.default.memo(()=>{let{pathname:e}=(0,d.useLocation)(),o=(0,d.useNavigate)(),[n]=(0,d.useSearchParams)(),t="/"+e.split("/")[1],i=[t+"/"+e.split("/")[2]],[r,s]=(0,l.useState)(i),[p,c]=(0,l.useState)(i),u=m.find(e=>e.path===t),f=w((null==u?void 0:u.routes)||[]);(0,l.useEffect)(()=>{let o=e.split("/"),n=o.reduce((e,n,t)=>(n&&e.push(o.slice(0,t+1).join("/")),e),[]);s(n),c(n);},[e]);let[b,k]=(0,l.useState)(!1);return(0,a.jsxs)(x.SiderContainer,{children:[(0,a.jsx)(x.SiderBtnContainer,{onClick:()=>k(e=>!e),children:(0,a.jsx)("span",{className:"shape",children:(0,a.jsx)("span",{className:"shapeContent",children:(0,a.jsx)(h.default,{className:"icon",rotate:b?180:0})})})}),(0,a.jsx)(x.StyleMenu,{mode:"inline",onClick:e=>{let{key:t}=e;o((0,g.urlPathWithQuery)(t,[],n));},selectedKeys:r,openKeys:p,onOpenChange:c,items:f,inlineCollapsed:b})]});});function k(){return(0,a.jsxs)("div",{style:{display:"flex",height:"100%",overflow:"hidden"},children:[(0,a.jsx)(b,{}),(0,a.jsx)("div",{style:{flex:1,height:"100%",overflow:"hidden"},children:(0,a.jsx)(d.Outlet,{})})]});}},"5e00c259":function(e,o,n){"use strict";n.d(o,"__esModule",{value:!0}),n.d(o,"Title",{enumerable:!0,get:function(){return r;}});var t=n("32b7a2cf"),i=n("5b5ed4a9");n("4d0e37ae");var a=n("fc1f4356");let d=i.styled.div`
  .header {
    color: var(--dark-shade-85);
    font-weight: var(--font-medium);
    font-size: ${({$level:e})=>"page"===e?"var(--h1-font-size)":"var(--h3-font-size)"};
    line-height: ${({$level:e})=>"page"===e?"28px":"24px"};
  }
`,r=({className:e,style:o,level:n="page",title:r,titleExtra:l,showBack:s=!1,goBackCb:p,children:g})=>{let c=(0,i.useNavigate)();return(0,t.jsxs)(d,{$level:n,className:e,style:o,children:[(0,t.jsxs)("div",{className:"flex-row mb16",children:[(0,t.jsxs)("div",{className:"header",children:[s&&(0,t.jsx)(i.Icon,{className:"icon mr8 pointer",icon:"ant-design:arrow-left-outlined",onClick:()=>(0,a.isFunction)(p)?p():c(-1)}),r]}),(0,t.jsx)("div",{children:l})]}),(0,t.jsx)("div",{children:g})]});};},a5eab18a:function(e,o,n){"use strict";n.d(o,"__esModule",{value:!0}),n.e(o,{DrawerStyle:function(){return p;},PropertyTable:function(){return u;},PropertyTitle:function(){return f;},RadioGroup:function(){return g;},SiderBtn:function(){return w;},SiderBtnContainer:function(){return b;},SiderContainer:function(){return m;},StyleMenu:function(){return x;},StyledDescriptions:function(){return h;},TabBar:function(){return c;}});var t=n("777fffbe"),i=n("5e00c259"),a=n("5b5ed4a9"),d=t._(n("9ffd6a0f")),r=t._(n("b09e51ac")),l=t._(n("097087ae")),s=t._(n("c7f4f8e9"));let p=(0,a.createGlobalStyle)`
  .akg-components-drawer {
    .ant-drawer-header {
      border-bottom: none;
    }
    .akg-components-drawer-body{
      height: 100%;
    }
  }
`,g=(0,a.styled)(l.default.Group)`
  position: absolute;
  left: 24px;
  top: 24px;
`,c=(0,a.styled)(l.default.Group)`
  position: sticky;
  z-index: 10;
  top: 0;
  padding: 0px 24px 16px !important;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
`,u=(0,a.styled)(s.default)`
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
`,f=(0,a.styled)(i.Title)`
  position: sticky;
  top: 49px;
  z-index: 6;
  padding: 8px 16px 8px 24px;
  background: #fff;

  & > div {
    margin-bottom: 0;
  }
`,h=(0,a.styled)(d.default)`
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
`,x=(0,a.styled)(r.default)`
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
`,w=a.styled.div`
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
`;}}]);
