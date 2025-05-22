(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["0a44ea72"],{"036e469d":function(e,t,a){a.d(t,"__esModule",{value:!0}),a.e(t,{default:function(){return ei;}});var i,o,n,d,l=a("777fffbe"),r=a("852bbaa9"),s=a("32b7a2cf"),c=a("f02131d0"),p=l._(c);(i=n||(n={})).TIME_ASC="time_asc",i.TIME_DESC="time_desc";let h={time_asc:p.default.get({id:"spg.src.constants.projectManager.FromOldToNewBy",dm:"\u6309\u65F6\u95F4\u4ECE\u65E7\u5230\u65B0"}),time_desc:p.default.get({id:"spg.src.constants.projectManager.ByTimeFromNewTo",dm:"\u6309\u65F6\u95F4\u4ECE\u65B0\u5230\u65E7"})};(o=d||(d={})).GENERAL="GENERAL",o.ANALYZABLE="ANALYZABLE",o.DEMO="DEMO";var f=a("cd8b2a5f"),g=a("07059fbf"),u=l._(g),m=a("4c4535f5"),v=a("5b5ed4a9"),b=a("d5b565e0"),x=l._(b),j=a("673e678e"),A=l._(j),P=a("4695e06b"),N=l._(P),w=a("fc1e2430"),C=l._(w),Z=a("1e70bad6"),I=l._(Z),D=a("4d0e37ae"),G=r._(D),L=a("1043743b"),S=l._(L),V=a("02b696d0"),y=r._(V),z={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M880 298.4H521L403.7 186.2a8.15 8.15 0 00-5.5-2.2H144c-17.7 0-32 14.3-32 32v592c0 17.7 14.3 32 32 32h736c17.7 0 32-14.3 32-32V330.4c0-17.7-14.3-32-32-32zM632 577c0 3.8-3.4 7-7.5 7H540v84.9c0 3.9-3.2 7.1-7 7.1h-42c-3.8 0-7-3.2-7-7.1V584h-84.5c-4.1 0-7.5-3.2-7.5-7v-42c0-3.8 3.4-7 7.5-7H484v-84.9c0-3.9 3.2-7.1 7-7.1h42c3.8 0 7 3.2 7 7.1V528h84.5c4.1 0 7.5 3.2 7.5 7v42z"}}]},name:"folder-add",theme:"filled"},O=a("833883d6"),T=l._(O),H=y.forwardRef(function(e,t){return y.createElement(T.default,(0,S.default)((0,S.default)({},e),{},{ref:t,icon:z}));}),M=a("d83b57bf"),U=l._(M),k=a("80411155"),J=l._(k),E=a("5f54387e"),W=l._(E),Y=a("6d1765ea"),B=l._(Y);a("74667962");var F=a("ea5a09e9"),K=l._(F),q=a("249809ea"),X=l._(q);let R=e=>{let{text:t,total:a}=e,[i,o]=(0,G.useState)(!1),n=(0,G.useRef)(null),d=(0,G.useRef)(null);return(0,G.useEffect)(()=>{let e=()=>{let e=d.current,t=n.current;t&&e&&o(t.scrollWidth>e.clientWidth);},t=new ResizeObserver(t=>{for(let a of t)e();});return d.current&&t.observe(d.current),()=>{t.disconnect();};},[]),(0,s.jsxs)("div",{ref:d,style:{display:"flex",width:"100%",overflow:"hidden"},children:[(0,s.jsx)("div",{ref:n,style:{whiteSpace:"nowrap",overflow:"hidden",textOverflow:"ellipsis"},children:t}),i&&(0,s.jsx)("div",{style:{whiteSpace:"nowrap"},children:p.default.get({id:"spg.Home.components.EllipsisTextWithCount.WaitingForTotalPeople",dm:"\u7B49{total}\u4EBA"},{total:a})})]});},Q=v.styled.div`
  .popLabelContainer {
    color: rgba(0, 0, 0, 45%);
    display: flex;
    align-items: center;

    .popLabel {
      color: rgba(0, 0, 0, 81%);
      &::after {
        content: '：';
      }
    }
  }
  .descContainer {
    width: 100%;
    background: #f6f8ff;
    border-radius: var(--border-radius-lg);
    color: var(--deep-blue-47);
    padding: 8px;
  }
`,_=e=>{let{projectData:t,children:a}=e,{id:i=0,name:o,description:n,namespace:d,ownersNameZh:l}=t||{},r=l||[];return(0,s.jsx)(K.default,{overlayInnerStyle:{width:398},placement:"bottom",autoAdjustOverflow:!0,overlayClassName:"container",open:!1,content:(0,s.jsxs)(Q,{children:[(0,s.jsxs)(X.default,{size:8,direction:"vertical",style:{marginBottom:16},children:[(0,s.jsxs)("div",{className:"popLabelContainer",children:[(0,s.jsx)("div",{className:"popLabel",children:p.default.get({id:"spg.Home.components.ProjectInfo.ProjectName",dm:"\u9879\u76EE\u540D\u79F0"})}),(0,s.jsx)("div",{children:o})]}),(0,s.jsxs)(X.default,{children:[(0,s.jsxs)("div",{className:"popLabelContainer",children:[(0,s.jsx)("div",{className:"popLabel",children:p.default.get({id:"spg.Home.components.ProjectInfo.ProjectId",dm:"\u9879\u76EEID"})}),(0,s.jsx)("div",{children:i})]}),(0,s.jsxs)("div",{className:"popLabelContainer",children:[(0,s.jsx)("div",{className:"popLabel",children:p.default.get({id:"spg.Home.components.ProjectInfo.NodePrefix",dm:"\u8282\u70B9\u524D\u7F00"})}),(0,s.jsx)("div",{children:d})]})]}),r.length>0&&(0,s.jsxs)("div",{className:"popLabelContainer",children:[(0,s.jsx)("div",{className:"popLabel",children:p.default.get({id:"spg.Home.components.ProjectInfo.ProjectAdministrator",dm:"\u9879\u76EE\u7BA1\u7406\u5458"})}),(0,s.jsx)("div",{style:{maxWidth:260},children:(0,s.jsx)(R,{text:r.join("\u3001"),total:r.length})})]})]}),(0,s.jsx)("div",{className:"descContainer",children:n})]}),children:a});},$=(0,v.styled)(J.default)`
  overflow: hidden;
  border-radius: var(--border-radius-lg);
  /* width: 382px; */
  cursor: default;

  .ant-card-head-wrapper {
    height: 49px;
    .ant-card-head-title {
      height: 100%;
      padding: 0;
    }
  }

  .ant-card-body {
    height: 96px;
    padding: 0;

    img {
      height: 100%;
    }
  }

  .dataBox {
    display: flex;
    flex-direction: row;
    align-items: center;
    height: 100%;
    padding: 24px;
    font-size: 14;

    .dataDetail {
      flex: 1;
      font-size: 12px;
      color: var(--deep-blue-47);
      > span {
        font-size: 20px;
        color: var(--deep-blue-88);
      }
    }
  }

  .imgContainer {
    height: 100%;
    text-align: center;
  }

  .titleContainer {
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;

    .icon {
      color: var(--deep-blue-47);
    }
  }

  .title {
    display: flex;
    align-items: center;
  }

  .action-btn {
    color: var(--deep-blue-47) !important;
  }
`,{Text:ee}=B.default,et=e=>{let t;let a=(0,v.useNavigate)(),{projectData:i={},refetchProjList:o}=e,{id:n=0,hasPermission:l,name:r,namespace:c,status:h,entityNum:f,relationNum:g,projectTag:u,config:m=""}=i,b=u===d.DEMO;return(0,s.jsx)($,{headStyle:{padding:0},hoverable:!0,title:(t=r,b&&(t=p.default.get({id:"spg.Home.components.ProjectCard.ExperienceTheDemoMap",dm:"\u4F53\u9A8CDEMO\u56FE\u8C31"})),(0,s.jsx)(_,{projectData:i,children:(0,s.jsx)("div",{className:"titleContainer",children:(0,s.jsx)("div",{className:"title",children:(0,s.jsxs)("div",{style:{maxWidth:180},children:[(0,s.jsx)(ee,{className:"mr8",ellipsis:{tooltip:t},children:t}),b&&(0,s.jsx)(W.default,{color:"blue",children:"Demo"})]})})})})),actions:[(0,s.jsx)(v.Link,{className:"action-btn",to:`/knowledgeModeling/knowledgeTask?projectId=${n}`,children:p.default.get({id:"spg.Home.components.ProjectCard.KnowledgeBuilding",dm:"\u77E5\u8BC6\u5E93\u7BA1\u7406"})},"knowledge-model"),(0,s.jsx)(v.Link,{className:"action-btn",to:`/analysisReasoning?projectId=${n}`,children:p.default.get({id:"spg.Home.components.ProjectCard.KnowledgeBaseQA",dm:"\u77E5\u8BC6\u5E93\u95EE\u7B54"})},"analysis-reason"),(0,s.jsx)("span",{className:"action-btn",onClick:()=>a(`/projectConfig/config/project?projectId=${n}`),children:p.default.get({id:"spg.Home.components.ProjectCard.ProjectConfiguration",dm:"\u77E5\u8BC6\u5E93\u914D\u7F6E"})},"project-config")],children:(0,s.jsx)("div",{className:"imgContainer",children:(0,s.jsx)("img",{src:U.default,alt:"demo project image"})})});},ea=v.styled.div`
  padding: var(--padding-md) 108px;
  .header {
    margin-bottom: var(--margin-md);
    .title {
      font-size: var(--h1-font-size);
      line-height: 28px;
      font-weight: var(--font-medium);
    }
    .ant-select-selector {
      .ant-select-selection-item {
        color: var(--deep-blue-68) !important;
      }
    }
    .ant-input-search {
      .ant-input {
        border-right: 1px solid transparent;
        &:hover,
        &:active,
        &:focus {
          border-right: 1px solid #597ef7;
        }
      }
      .ant-input-group-addon {
        background-color: #fff;
      }
      .ant-input-search-button {
        background-color: transparent;
        border-left: 1px solid transparent;
        &:hover,
        &:active,
        &:focus {
          border-left: 1px solid #597ef7;
        }
        .anticon-search {
          color: var(--deep-blue-35);
        }
      }
    }
  }
  .project-list {
    display: grid;
    grid-gap: 24px;
    grid-template-columns: repeat(4, minmax(calc(25% - 16px), 1fr));
    width: 100%;
    margin-bottom: 8px;

    .empty-project-create {
      height: 192px;
      color: #2f54eb;
      font-size: 16px;
      font-weight: 500;
      background: transparent;
      border-width: 2px;

      .anticon-folder-add {
        font-size: 20px;
      }
    }
  }
  @media screen and (max-width: 1440px) {
    .project-list {
      grid-template-columns: repeat(3, minmax(calc(33.3% - 16px), 1fr));
    }
  }
`,ei=()=>{var e;let t=(0,v.useNavigate)(),[a,i]=(0,G.useState)({sort:"desc",sortBy:"time"}),[o,d]=(0,G.useState)(),{page:l,size:r,getPagination:c,resetPageNumber:g,changePagination:b}=(0,u.default)(11),{data:{result:j}={},refetch:P,isFetching:w}=(0,v.useQuery)(["getProjectList",{...a,searchKey:o,page:l,size:r}],()=>(0,m.getProjectList)({...a,keyword:o,page:l,size:r})),Z=(0,f.useMemoizedFn)(()=>{let e=(window.innerWidth>1440?4:3)*Math.max(Math.floor((window.innerHeight-148+24)/218),1)-1;e!==r&&b(Math.max(Math.floor(Math.min((null==j?void 0:j.total)||0,r*l)/e),1),e);});return(0,G.useEffect)(()=>(window.addEventListener("resize",Z),Z(),()=>{window.removeEventListener("resize",Z);}),[]),(0,s.jsxs)(ea,{children:[(0,s.jsxs)("div",{className:"header flex-row",children:[(0,s.jsx)("div",{className:"title",children:p.default.get({id:"spg.pages.Home.ProjectList",dm:"\u77E5\u8BC6\u5E93\u5217\u8868"})}),(0,s.jsxs)("div",{className:"extra-right-content",children:[(0,s.jsx)(C.default,{value:`${a.sortBy}_${a.sort}`,style:{width:220},options:Object.values(n).map(e=>({label:h[e],value:e})),onChange:e=>{let[t,a]=e.split("_");i({sortBy:t,sort:a}),g();}}),(0,s.jsx)(A.default.Search,{onSearch:e=>{g(),d(e);},className:"ml8 mr8",style:{width:212},placeholder:p.default.get({id:"spg.pages.Home.EnterProjectName",dm:"\u8F93\u5165\u9879\u76EE\u540D\u79F0"})}),(0,s.jsx)(x.default,{type:"default",onClick:()=>t("/home/globalConfig"),children:p.default.get({id:"spg.pages.Home.GlobalConfiguration",dm:"\u5168\u5C40\u914D\u7F6E"})})]})]}),(0,s.jsx)(I.default,{spinning:w,children:(0,s.jsxs)("div",{className:"project-list",children:[(0,s.jsxs)(x.default,{className:"empty-project-create",type:"dashed",onClick:()=>t("/home/create"),children:[(0,s.jsx)(H,{}),p.default.get({id:"spg.pages.Home.CreateKnowledgeBase",dm:"\u521B\u5EFA\u77E5\u8BC6\u5E93"})]}),null==j?void 0:null===(e=j.results)||void 0===e?void 0:e.map(e=>(0,s.jsx)(et,{projectData:e,refetchProjList:P},e.id))]})}),(0,s.jsx)(N.default,{style:{textAlign:"right"},hideOnSinglePage:!0,...c({total:null==j?void 0:j.total,showSizeChanger:!1})})]});};},"07059fbf":function(e,t,a){"use strict";a.d(t,"__esModule",{value:!0}),a.d(t,"default",{enumerable:!0,get:function(){return d;}});var i=a("777fffbe")._(a("f02131d0")),o=a("4d0e37ae");let n={showSizeChanger:!0};function d(e){let[t,a]=(0,o.useState)(e??10),[d,l]=(0,o.useState)(1),r=t*(d-1)+1,s=t*d,c=e=>i.default.get({id:"spg.src.hooks.usePagination.StartinfonoArticleEndinfonoTotalIn",dm:"\u7B2C {startInfoNo} - {endInfoNo} \u6761 / \u5171 {total} \u6761 "},{startInfoNo:r,endInfoNo:s,total:e}),p=(e,t)=>{a(t),l(e);},h=({total:e=0,showTotal:a,...i})=>({pageSize:t,current:d,total:e,getPagination:h,onChange:p,showTotal:a?c:void 0,...n,...i});return{pageSize:t,pageNumber:d,page:d,size:t,limit:t,resetPageNumber:()=>{l(1);},getPagination:h,changePagination:p};}},74667962:function(e,t,a){"use strict";a.d(t,"__esModule",{value:!0}),a("93b3369a");},d83b57bf:function(e,t,a){e.exports="data:image/png;base64,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";}}]);
