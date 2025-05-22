(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["f077292b"],{"0a75b646":function(e,l,t){"use strict";t.d(l,"__esModule",{value:!0}),t.d(l,"default",{enumerable:!0,get:function(){return i;}});var d=t("777fffbe"),a=t("32b7a2cf"),o=d._(t("d5b565e0")),n=d._(t("0c8a2b89")),i=d._(t("4d0e37ae")).default.memo(e=>{let{tip:l,tooltipProps:t,children:d,...i}=e;return(0,a.jsx)(n.default,{...t,title:l,children:(0,a.jsx)(o.default,{...i,children:d})});});},"24c8531b":function(e,l,t){t.d(l,"__esModule",{value:!0}),t.e(l,{default:function(){return k;}});var d=t("777fffbe"),a=t("852bbaa9"),o=t("32b7a2cf"),n=t("5b5ed4a9"),i=t("5661e780"),r=d._(i),s=t("4d0e37ae"),u=a._(s),m=t("c5f39a1b"),c=d._(m),f=t("f02131d0"),p=d._(f),g=t("3fe68e88"),v=d._(g),b=t("fd7ca954"),h=t("4c1838f3"),x=d._(h);let{Item:C}=v.default,j=n.styled.div`
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
`;var y=t("5e00c259");let _=(0,n.styled)(e=>{let{items:l,wrapperStyle:t,...d}=e,a=(0,n.useSelectedRoutes)(),i=(0,n.useNavigate)(),[r]=(0,n.useSearchParams)(),s=(0,u.useMemo)(()=>{if(l)return l;let e=[];return null==a||a.forEach((l,t)=>{var d,o,n,i,r,s,u,m;(null==l?void 0:null===(d=l.route)||void 0===d?void 0:d.name)&&((0,x.default)(null==l?void 0:null===(o=l.route)||void 0===o?void 0:o.name)?e.push({key:l.pathnameBase,title:p.default.get(null==l?void 0:null===(n=l.route)||void 0===n?void 0:n.name),href:t+1!==a.length?(null==l?void 0:null===(i=l.route)||void 0===i?void 0:i.navPath)||(null==l?void 0:null===(r=l.route)||void 0===r?void 0:r.path):void 0}):e.push({key:l.pathnameBase,title:null==l?void 0:null===(s=l.route)||void 0===s?void 0:s.name,href:t+1!==a.length?(null==l?void 0:null===(u=l.route)||void 0===u?void 0:u.navPath)||(null==l?void 0:null===(m=l.route)||void 0===m?void 0:m.path):void 0}));}),e;},[a]),m=e=>{i((0,b.urlPathWithQuery)(e,[],r));};return(0,o.jsx)(j,{style:{...t},children:(0,o.jsx)(v.default,{...d,children:s.map(e=>(0,o.jsx)(C,{children:e.href?(0,o.jsx)("a",{className:"link-breadcrumb-item",onClick:()=>m(e.href),children:e.title}):(0,o.jsx)("span",{className:"span-breadcrumb-item",children:e.title})},e.key))})});})`
  &.breadcrumb {
    margin-bottom: 10px;
  }
`,M=n.styled.div`
  position: relative;
  padding: 0 var(--padding-lg) var(--padding-lg);
  &.layout-with-breadcrumb {
    padding-top: 12px;
  }
  &.layout-without-breadcrumb {
    padding-top: 24px;
  }
`,k=({children:e,breadcrumb:l=!1,loading:t,title:d,goBack:a,showBack:n,titleProps:i,className:s,...u})=>{if(t)return(0,o.jsx)(r.default,{active:!0});let m=!!l;return(0,o.jsxs)(M,{...u,className:(0,c.default)([{"layout-without-breadcrumb":!m},{"layout-with-breadcrumb":m},s]),children:[!1!==l&&(0,o.jsx)(_,{className:"breadcrumb",...l}),(d||(null==i?void 0:i.title))&&(0,o.jsx)(y.Title,{level:"page",title:d,goBackCb:a,showBack:n,...i}),e]});};},"2ac83fa7":function(e,l,t){"use strict";t.d(l,"__esModule",{value:!0}),t.d(l,"default",{enumerable:!0,get:function(){return g;}});var d=t("777fffbe"),a=t("32b7a2cf"),o=d._(t("24c8531b")),n=t("df7e9d64"),i=d._(t("f02131d0")),r=d._(t("3c61cd15")),s=t("4c4535f5"),u=t("5b5ed4a9"),m=d._(t("2b798761")),c=d._(t("1e70bad6")),f=t("3834a44f"),p=t("e636800f"),g=()=>{var e;let{userInfo:l}=(0,n.useUserInfo)({resourceTag:"PROJECT",resourceId:Number((0,f.getProjectId)())}),t=null==l?void 0:null===(e=l.permissionList)||void 0===e?void 0:e.some(e=>{var l;return(null===(l=e.accountRoleInfo)||void 0===l?void 0:l.roleName)&&["SUPER","OWNER"].includes(e.accountRoleInfo.roleName);}),{data:d,isFetching:g,refetch:v}=(0,p.useProjectInfo)(),{run:b,loading:h}=(0,u.useRequest)(s.update,{manual:!0,formatResult:e=>(e.success&&(m.default.success(i.default.get({id:"spg.ProjectConfig.Config.Modal.UpdateSucceeded",dm:"\u66F4\u65B0\u6210\u529F"})),v()),e)});return(0,a.jsx)(o.default,{titleProps:{title:i.default.get({id:"spg.ProjectConfig.Config.Modal.ModelConfiguration",dm:"\u6A21\u578B\u914D\u7F6E"})},children:(0,a.jsx)(c.default,{spinning:g||h,children:(0,a.jsx)(r.default,{ableEdit:t,disabledTip:i.default.get({id:"spg.ProjectConfig.Config.Modal.OwnerOrSuperTubeOnly",dm:"\u4EC5Owner\u6216\u8D85\u7BA1\u53EF\u64CD\u4F5C"}),dataSource:d.config.llm_select||[],onSubmit:(e,l=()=>{})=>{let t={id:d.id,name:d.name,namespace:d.namespace,description:d.description,config:"{}"},a=JSON.stringify({graph_store:d.config.graph_store,vectorizer:d.config.vectorizer,prompt:d.config.prompt,llm:d.config.llm,...e,llm_select:e.llm_select||[]});t.config=a,b({id:d.id},t).then(l);},submitting:h})})});};},"3c61cd15":function(e,l,t){t.d(l,"__esModule",{value:!0}),t.e(l,{default:function(){return ea;}});var d=t("777fffbe"),a=t("852bbaa9"),o=t("32b7a2cf"),n=t("0a75b646"),i=d._(n),r=t("df7e9d64"),s=t("f02131d0"),u=d._(s),m=t("d5b565e0"),c=d._(m),f=t("80411155"),p=d._(f),g=t("b916d24f"),v=d._(g),b=t("c7d6ff82"),h=d._(b),x=t("c7f4f8e9"),C=d._(x),j=t("e6fc687f"),y=d._(j),_=t("4d0e37ae"),M=a._(_),k=t("fc1f4356"),P=t("7bbc7a67"),I=d._(P),N=t("dad61df1"),E=d._(N),F=t("8f891262"),T=d._(F),w=t("673e678e"),S=d._(w),D=t("5c03c6a7"),O=d._(D),A=t("c60e02e5"),q=d._(A),R=t("249809ea"),B=d._(R),L=t("d86057ae"),K=d._(L),Y=t("7cf502f9"),V=d._(Y),z="customParamSpace-XOL4_ALF",W="modelConfigModal-dUZ7yIot";let U=e=>{let{src:l="",style:t={},size:d=56,ml:a=0,mr:n=0}=e;return(0,o.jsx)("img",{style:{width:d,height:d,borderRadius:"50%",border:"none",marginLeft:a,marginRight:n,...t},src:l});},$=e=>{var l;let{modelInfo:t,open:d,onCancel:a,onSubmit:n,loading:i}=e,[r]=T.default.useForm();return(0,o.jsx)(q.default,{afterClose:()=>{r.resetFields();},title:(0,o.jsxs)("span",{className:"flex-row-start",children:[(0,o.jsx)(U,{src:t.logo,size:24,mr:8}),(0,o.jsx)("span",{children:u.default.get({id:"spg.components.ModelConfigComp.AddModelComp.AddNewModel",dm:"\u6DFB\u52A0\u65B0\u6A21\u578B"})})]}),open:d,onOk:()=>{r.validateFields().then(e=>{n(e);});},onCancel:()=>{i||a();},okButtonProps:{loading:!!i},cancelButtonProps:{loading:!!i},className:W,maskClosable:!1,destroyOnClose:!0,children:(0,o.jsxs)(T.default,{form:r,children:[(0,o.jsx)(T.default.Item,{label:"",name:"llm_id",hidden:!0,children:(0,o.jsx)(S.default,{disabled:!0})}),null===(l=t.params)||void 0===l?void 0:l.sort((e,l)=>"model"===e.ename?-1:"model"===l.ename?1:0).map(e=>(0,o.jsx)(T.default.Item,{initialValue:e.defaultValue,label:e.cname,name:["otherParams",e.ename],rules:[{required:e.required,message:u.default.get({id:"spg.components.ModelConfigComp.AddModelComp.PleaseEnterItemcname",dm:"\u8BF7\u8F93\u5165{itemCname}"},{itemCname:e.cname})}],children:"number"===e.formType?(0,o.jsx)(O.default,{...e.formProps||{},style:{width:"100%"}}):(0,o.jsx)(S.default,{...e.formProps||{}})},e.ename)),(0,o.jsx)(E.default,{children:u.default.get({id:"spg.components.ModelConfigComp.AddModelComp.CustomFields",dm:"\u81EA\u5B9A\u4E49\u5B57\u6BB5"})}),(0,o.jsx)(T.default.List,{name:"customParams",children:(e,{add:l,remove:d})=>(0,o.jsxs)(o.Fragment,{children:[e.map(({key:e,name:l,...a})=>(0,o.jsx)(o.Fragment,{children:(0,o.jsxs)(B.default,{className:z,align:"baseline",children:[(0,o.jsx)(T.default.Item,{name:[l,"key"],label:"",rules:[{required:!0,message:`\u{8BF7}\u{8F93}\u{5165}\u{81EA}\u{5B9A}\u{4E49}\u{5B57}\u{6BB5}${l+1}key`},{validator:(e,d)=>{var a,o;return(null===(a=t.params)||void 0===a?void 0:a.map(e=>null==e?void 0:e.ename).filter(Boolean)).includes(d)?Promise.reject(u.default.get({id:"spg.components.ModelConfigComp.AddModelComp.DuplicateWithExistingField",dm:"\u4E0E\u5DF2\u6709\u5B57\u6BB5\u91CD\u590D"})):((null===(o=r.getFieldsValue())||void 0===o?void 0:o.customParams)||[]).filter((e,t)=>t!==l).map(e=>null==e?void 0:e.key).includes(d)?Promise.reject(u.default.get({id:"spg.components.ModelConfigComp.AddModelComp.DuplicateWithOtherCustomFields",dm:"\u4E0E\u5176\u4ED6\u81EA\u5B9A\u4E49\u5B57\u6BB5\u91CD\u590D"})):Promise.resolve();}}],...a,children:(0,o.jsx)(S.default,{})}),":",(0,o.jsx)(T.default.Item,{name:[l,"value"],label:"",rules:[{required:!0,message:`\u{8BF7}\u{8F93}\u{5165}\u{81EA}\u{5B9A}\u{4E49}\u{5B57}\u{6BB5}${l+1}value`}],...a,children:(0,o.jsx)(S.default,{})}),(0,o.jsx)(K.default,{className:"dynamic-delete-button",onClick:()=>d(l)})]},e)})),(0,o.jsx)(c.default,{type:"dashed",onClick:()=>l(),style:{width:"100%"},icon:(0,o.jsx)(V.default,{}),children:u.default.get({id:"spg.components.ModelConfigComp.AddModelComp.AddCustomField",dm:"\u6DFB\u52A0\u81EA\u5B9A\u4E49\u5B57\u6BB5"})})]})})]})});};var H=t("240ff6a0"),G=t("5b5ed4a9"),Q=t("073a1148");let{json2Object:J}=a._(Q),X=()=>{let{isFetching:e,data:l=[]}=(0,G.useQuery)({queryFn:async()=>{let e=await (0,H.getConfigListByCondition)({configId:"KAG_SUPPORT_MODEL",version:"1"});if(e.success&&e.result)return J(e.result.config||"[]");},queryKey:["KAG_SUPPORT_MODEL"]});return{isFetching:e,data:l};},Z=(0,M.forwardRef)((e,l)=>{let{record:t={},open:d,onOk:a,onClose:n,loading:i}=e,{data:r}=X(),[s]=T.default.useForm();(0,M.useImperativeHandle)(l,()=>({form:s})),(0,M.useEffect)(()=>{var e;s.setFieldValue("customParams",null===(e=t.__customParamKeys)||void 0===e?void 0:e.map(e=>({key:e,value:t[e]})));},[t]);let m=null==r?void 0:r.find(e=>e.vendor===t.type);return(0,o.jsx)(q.default,{title:(0,o.jsxs)("span",{className:"flex-row-start",children:[m&&(0,o.jsx)(U,{src:m.logo,size:24,mr:8}),(0,o.jsx)("span",{children:u.default.get({id:"spg.components.ModelConfigComp.EditModelComp.EditModel",dm:"\u7F16\u8F91\u6A21\u578B"})})]}),open:d,onOk:a,onCancel:n,okButtonProps:{loading:i},cancelButtonProps:{loading:i},className:W,maskClosable:!1,destroyOnClose:!0,children:(0,o.jsxs)(T.default,{form:s,children:[Object.keys(t).sort((e,l)=>{var t;let d={model:9999};return null==m||null===(t=m.params)||void 0===t||t.forEach((e,l)=>{d[e.ename]=l;}),(d[e]||0)-(d[l]||0);}).map(e=>{var l;let d=null==m?void 0:null===(l=m.params)||void 0===l?void 0:l.find(l=>l.ename===e);if(["createTime","creator"].includes(e)||"__customParamKeys"===e)return null;let a=t.__customParamKeys;return Array.isArray(a)&&a.includes(e)?null:(0,o.jsx)(T.default.Item,{label:e,name:e,hidden:["default","llm_id","type"].includes(e),rules:[{required:null==d?void 0:d.required,message:u.default.get({id:"spg.components.ModelConfigComp.EditModelComp.PleaseEnterKey",dm:"\u8BF7\u8F93\u5165{key}"},{key:e})}],children:(null==d?void 0:d.formType)==="number"?(0,o.jsx)(O.default,{...(null==d?void 0:d.formProps)||{},style:{width:"100%"}}):(0,o.jsx)(S.default,{...(null==d?void 0:d.formProps)||{},disabled:["model"].includes(e)})},e);}),(()=>{let e=(t.__customParamKeys||[]).map(e=>({key:e,value:t[e]}));return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(E.default,{children:u.default.get({id:"spg.components.ModelConfigComp.EditModelComp.CustomFields",dm:"\u81EA\u5B9A\u4E49\u5B57\u6BB5"})}),(0,o.jsx)(T.default.List,{name:"customParams",initialValue:e,children:(e,{add:l,remove:t})=>(0,o.jsxs)(o.Fragment,{children:[e.map(({key:e,name:l,...d})=>(0,o.jsx)(o.Fragment,{children:(0,o.jsxs)(B.default,{className:z,align:"baseline",children:[(0,o.jsx)(T.default.Item,{name:[l,"key"],label:"",rules:[{required:!0,message:`\u{8BF7}\u{8F93}\u{5165}\u{81EA}\u{5B9A}\u{4E49}\u{5B57}\u{6BB5}${l+1}key`},{validator:(e,t)=>{var d,a;return((null==m?void 0:null===(d=m.params)||void 0===d?void 0:d.map(e=>null==e?void 0:e.ename).filter(Boolean))||[]).includes(t)?Promise.reject(u.default.get({id:"spg.components.ModelConfigComp.EditModelComp.DuplicateWithExistingField",dm:"\u4E0E\u5DF2\u6709\u5B57\u6BB5\u91CD\u590D"})):((null===(a=s.getFieldsValue())||void 0===a?void 0:a.customParams)||[]).filter((e,t)=>t!==l).map(e=>null==e?void 0:e.key).includes(t)?Promise.reject(u.default.get({id:"spg.components.ModelConfigComp.EditModelComp.DuplicateWithOtherCustomFields",dm:"\u4E0E\u5176\u4ED6\u81EA\u5B9A\u4E49\u5B57\u6BB5\u91CD\u590D"})):Promise.resolve();}}],...d,children:(0,o.jsx)(S.default,{})}),":",(0,o.jsx)(T.default.Item,{name:[l,"value"],label:"",rules:[{required:!0,message:`\u{8BF7}\u{8F93}\u{5165}\u{81EA}\u{5B9A}\u{4E49}\u{5B57}\u{6BB5}${l+1}value`}],...d,children:(0,o.jsx)(S.default,{})}),(0,o.jsx)(K.default,{className:"dynamic-delete-button",onClick:()=>t(l)})]},e)})),(0,o.jsx)(c.default,{type:"dashed",onClick:()=>l(),style:{width:"100%"},icon:(0,o.jsx)(V.default,{}),children:u.default.get({id:"spg.components.ModelConfigComp.EditModelComp.AddCustomField",dm:"\u6DFB\u52A0\u81EA\u5B9A\u4E49\u5B57\u6BB5"})})]})})]});})()]})});}),ee=G.styled.div`
  .op-btn {
    padding: 0;
    padding-right: 8px;

    & > button {
      padding: 0;
    }
  }

  .card-container {
    margin-bottom: 16px;

    p {
      margin: 0;
    }

    > .ant-tabs-card {
      .ant-tabs-content {
        margin-top: -16px;

        > .ant-tabs-tabpane {
          padding: 16px;
          background: #fff;
        }
      }

      > .ant-tabs-nav::before {
        display: none;
      }

      .ant-tabs-tab {
        background: transparent;
        border-color: transparent;
      }

      .ant-tabs-tab-active {
        background: #fff;
        border-color: #fff;
      }
    }
  }

  #components-tabs-demo-card-top {
    .code-box-demo {
      padding: 24px;
      overflow: hidden;
      background: #f5f5f5;
    }
  }

  [data-theme='compact'] {
    .card-container {
      > .ant-tabs-card {
        .ant-tabs-content {
          height: 120px;
          margin-top: -8px;
        }
      }
    }
  }

  [data-theme='dark'] {
    .card-container {
      > .ant-tabs-card {
        .ant-tabs-tab {
          background: transparent;
          border-color: transparent;
        }

        .ant-tabs-tab-active {
          background: #141414;
          border-color: #141414;
        }

        .ant-tabs-content {
          > .ant-tabs-tabpane {
            background: #141414;
          }
        }
      }
    }

    #components-tabs-demo-card-top {
      .code-box-demo {
        background: #000;
      }
    }
  }
`;var el=t("1e70bad6"),et=d._(el);let ed=e=>{let{ableEdit:l,disabledTip:t="",onAddModel:d}=e,{data:a,isFetching:n}=X();return(0,o.jsxs)(et.default,{spinning:n,children:[(0,o.jsx)("div",{children:u.default.get({id:"spg.components.ModelConfigComp.ModelList.ModelList",dm:"\u6A21\u578B\u5217\u8868"})}),(0,o.jsx)("div",{style:{display:"flex",gap:16,flexWrap:"wrap"},children:a.map(e=>(0,o.jsxs)(p.default,{style:{width:190,height:170,borderRadius:8},bodyStyle:{display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center",gap:8,paddingTop:16},children:[(0,o.jsx)(U,{src:e.logo}),(0,o.jsx)("div",{children:e.vendor}),(0,o.jsx)(i.default,{disabled:!l,tip:l?"":t,type:"default",icon:(0,o.jsx)(V.default,{}),onClick:()=>d(e),children:u.default.get({id:"spg.components.ModelConfigComp.ModelList.AddModel",dm:"\u6DFB\u52A0\u6A21\u578B"})})]},e.vendor))})]});};var ea=e=>{var l;let{ableEdit:t,disabledTip:d="",dataSource:a,submitting:n,onSubmit:s}=e,{userInfo:m}=(0,r.useUserInfo)(),{info:f}=m,g=(null==f?void 0:f.nickName)||(null==f?void 0:f.realName)||(null==f?void 0:f.workNo),b=a.filter(e=>!!e.llm_id),{data:x}=X(),[j,_]=(0,M.useState)(null===(l=x[0])||void 0===l?void 0:l.vendor);(0,M.useEffect)(()=>{if(b.length&&x.length){let l=b.map(e=>e.type),t=x.findIndex(e=>l.includes(e.vendor));if(-1!==t){var e;_(null===(e=x[t])||void 0===e?void 0:e.vendor);}}},[!x.length,!b.length]);let P=e=>{s({llm_select:[...b].filter(l=>!(0,k.isEqual)(l.llm_id,e))});},[N,E]=(0,M.useState)(!1),[F,T]=(0,M.useState)(!1),[w,S]=(0,M.useState)({}),[D,O]=(0,M.useState)({}),A=(0,M.useRef)(null),q=e=>{let l=(0,k.cloneDeep)(b),t=b.findIndex(l=>(0,k.isEqual)(l.llm_id,e));if(-1!==t){l[t].default=!0,l.forEach((t,d)=>{t.default&&!(0,k.isEqual)(t.llm_id,e)&&(l[d].default=!1);});let d={...l[t]};s({llm_select:l,llm:d});}},R=b.find(e=>!!e.default),B=[{title:u.default.get({id:"spg.components.ModelConfigComp.ModelId",dm:"\u6A21\u578BID"}),key:"llm_id",dataIndex:"llm_id"},{title:u.default.get({id:"spg.components.ModelConfigComp.ModelType",dm:"\u6A21\u578B\u540D\u79F0"}),key:"model",dataIndex:"model",render:(e,l)=>e||(null==l?void 0:l.scene_name)||"-"},{title:u.default.get({id:"spg.components.ModelConfigComp.Desc",dm:"\u5907\u6CE8"}),key:"desc",dataIndex:"desc"},{title:u.default.get({id:"spg.components.ModelConfigComp.Creator",dm:"\u521B\u5EFA\u4EBA"}),key:"creator",dataIndex:"creator"},{title:u.default.get({id:"spg.components.ModelConfigComp.ModificationTime",dm:"\u4FEE\u6539\u65F6\u95F4"}),key:"createTime",dataIndex:"createTime"},{title:u.default.get({id:"spg.components.ModelConfigComp.Operation",dm:"\u64CD\u4F5C"}),key:"operation",width:200,render:(e,l)=>(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(i.default,{className:"op-btn",type:"link",disabled:!t,tip:t?"":d,onClick:()=>{var e;null===(e=A.current)||void 0===e||e.form.setFieldsValue(l),O(l),E(!0);},children:u.default.get({id:"spg.components.ModelConfigComp.Edit",dm:"\u7F16\u8F91"})}),(0,o.jsx)(h.default,{title:u.default.get({id:"spg.components.ModelConfigComp.AreYouSureYouWant",dm:"\u786E\u5B9A\u5220\u9664\u8FD9\u4E2A\u6A21\u578B\u5417\uFF1F"}),okText:u.default.get({id:"spg.components.ModelConfigComp.Determine",dm:"\u786E\u5B9A"}),cancelText:u.default.get({id:"spg.components.ModelConfigComp.Cancel",dm:"\u53D6\u6D88"}),onConfirm:()=>{P(l.llm_id);},children:(0,o.jsx)(i.default,{disabled:!t||l.default,tip:t?l.default?u.default.get({id:"spg.components.ModelConfigComp.TheDefaultModelCannotBe",dm:"\u9ED8\u8BA4\u6A21\u578B\u4E0D\u53EF\u5220\u9664"}):"":d,className:"op-btn",type:"link",children:u.default.get({id:"spg.components.ModelConfigComp.Delete",dm:"\u5220\u9664"})})}),l.default?(0,o.jsx)(c.default,{type:"link",disabled:!0,className:"op-btn",children:u.default.get({id:"spg.components.ModelConfigComp.CurrentDefault",dm:"\u5F53\u524D\u9ED8\u8BA4"})}):(0,o.jsx)(i.default,{type:"link",className:"op-btn",disabled:!t,tip:t?R?u.default.get({id:"spg.components.ModelConfigComp.TheCurrentDefaultModelIs",dm:"\u5F53\u524D\u9ED8\u8BA4\u6A21\u578B\u4E3A:\n{defaultModelVendor}/{defaultModelClientType}/{defaultModelModel}"},{defaultModelVendor:R.vendor,defaultModelClientType:R.client_type,defaultModelModel:R.model}):u.default.get({id:"spg.components.ModelConfigComp.NoCurrentDefaultModel",dm:"\u5F53\u524D\u65E0\u9ED8\u8BA4\u6A21\u578B"}):d,onClick:()=>{q(l.llm_id);},children:u.default.get({id:"spg.components.ModelConfigComp.SetAsDefault",dm:"\u8BBE\u4E3A\u9ED8\u8BA4"})})]})}];return(0,o.jsxs)(ee,{children:[0===b.length?(0,o.jsx)(p.default,{style:{marginBottom:24},children:(0,o.jsx)(v.default,{image:v.default.PRESENTED_IMAGE_SIMPLE,description:u.default.get({id:"spg.components.ModelConfigComp.NoModelPleaseAddModel",dm:"\u6682\u65E0\u6A21\u578B\uFF0C\u8BF7\u6DFB\u52A0\u6A21\u578B"})})}):(0,o.jsx)("div",{className:"card-container",children:(0,o.jsx)(y.default,{type:"card",activeKey:j,onChange:_,items:x.map(e=>({label:(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(U,{src:e.logo,size:24,mr:8}),(0,o.jsx)("span",{children:e.vendor})]}),key:e.vendor,children:(0,o.jsx)(C.default,{showHeader:!0,columns:B,dataSource:b.filter(l=>e.vendor===l.type),pagination:!1,className:"tableComponent"})}))})}),(0,o.jsx)(ed,{ableEdit:t,disabledTip:d,onAddModel:e=>{S(e),T(!0);}}),(0,o.jsx)($,{modelInfo:w,open:F,loading:n,onSubmit:e=>{let l=[],t=(e.customParams||[]).reduce((e,t)=>(l.push(t.key),{...e,[t.key]:t.value}),{}),d={llm_id:e.llm_id,type:w.vendor,creator:g,createTime:(0,I.default)().format("YYYY-MM-DD HH:mm:ss"),...e.otherParams,...t,__customParamKeys:l};b.length||(d.default=!0);let a=[d,...b];s({llm_select:a,llm:a.find(e=>e.default)},()=>{T(!1),_(w.vendor);});},onCancel:()=>T(!1)}),(0,o.jsx)(Z,{open:N,onOk:()=>{var e,l;null===(l=A.current)||void 0===l||null===(e=l.form)||void 0===e||e.validateFields().then(e=>{let l=b.findIndex(e=>(0,k.isEqual)(e.llm_id,D.llm_id));if(-1!==l){let t;let d=(0,k.cloneDeep)(b),a=b[l].default;d[l]={...e,llm_id:D.llm_id,creator:g,createTime:(0,I.default)().format("YYYY-MM-DD HH:mm:ss"),default:a};let o=d[l];o.__customParamKeys=e.customParams.map(e=>e.key),(o.customParams||[]).forEach(e=>{o[e.key]=e.value;}),delete o.customParams,a&&(t={...o,default:!0}),s(t?{llm_select:d,llm:t}:{llm_select:d},()=>{O({}),E(!1);});}});},onClose:()=>{var e;n||(E(!1),O({}),null===(e=A.current)||void 0===e||e.form.resetFields());},ref:A,record:D,loading:n})]});};},"5e00c259":function(e,l,t){"use strict";t.d(l,"__esModule",{value:!0}),t.d(l,"Title",{enumerable:!0,get:function(){return i;}});var d=t("32b7a2cf"),a=t("5b5ed4a9");t("4d0e37ae");var o=t("fc1f4356");let n=a.styled.div`
  .header {
    color: var(--dark-shade-85);
    font-weight: var(--font-medium);
    font-size: ${({$level:e})=>"page"===e?"var(--h1-font-size)":"var(--h3-font-size)"};
    line-height: ${({$level:e})=>"page"===e?"28px":"24px"};
  }
`,i=({className:e,style:l,level:t="page",title:i,titleExtra:r,showBack:s=!1,goBackCb:u,children:m})=>{let c=(0,a.useNavigate)();return(0,d.jsxs)(n,{$level:t,className:e,style:l,children:[(0,d.jsxs)("div",{className:"flex-row mb16",children:[(0,d.jsxs)("div",{className:"header",children:[s&&(0,d.jsx)(a.Icon,{className:"icon mr8 pointer",icon:"ant-design:arrow-left-outlined",onClick:()=>(0,o.isFunction)(u)?u():c(-1)}),i]}),(0,d.jsx)("div",{children:r})]}),(0,d.jsx)("div",{children:m})]});};},c5f39a1b:function(e,l,t){"use strict";t.d(l,"__esModule",{value:!0}),t.d(l,"default",{enumerable:!0,get:function(){return d;}});var d=t("777fffbe")._(t("85d9e535")).default;},e636800f:function(e,l,t){"use strict";t.d(l,"__esModule",{value:!0}),t.d(l,"useProjectInfo",{enumerable:!0,get:function(){return r;}});var d=t("852bbaa9"),a=t("4c4535f5"),o=t("5b5ed4a9"),n=t("3834a44f");let{json2Object:i}=d._(t("073a1148")),r=()=>{let e=Number((0,n.getProjectId)()),{isFetching:l,data:t={config:{}},refetch:d}=(0,o.useQuery)({queryFn:async()=>{let l=await (0,a.getProjectInfo)({projectId:e});if(l.success&&l.result){let e=i(l.result.config||"{}");return{...l.result,config:e};}},queryKey:["getProjectInfo",e]});return{data:t,isFetching:l,refetch:d};};}}]);
