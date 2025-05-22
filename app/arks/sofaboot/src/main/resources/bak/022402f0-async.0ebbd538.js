(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["022402f0"],{"0a75b646":function(e,t,a){"use strict";a.d(t,"__esModule",{value:!0}),a.d(t,"default",{enumerable:!0,get:function(){return d;}});var l=a("777fffbe"),r=a("32b7a2cf"),i=l._(a("d5b565e0")),s=l._(a("0c8a2b89")),d=l._(a("4d0e37ae")).default.memo(e=>{let{tip:t,tooltipProps:a,children:l,...d}=e;return(0,r.jsx)(s.default,{...a,title:t,children:(0,r.jsx)(i.default,{...d,children:l})});});},"24c8531b":function(e,t,a){a.d(t,"__esModule",{value:!0}),a.e(t,{default:function(){return _;}});var l=a("777fffbe"),r=a("852bbaa9"),i=a("32b7a2cf"),s=a("5b5ed4a9"),d=a("5661e780"),o=l._(d),n=a("4d0e37ae"),u=r._(n),c=a("c5f39a1b"),f=l._(c),m=a("f02131d0"),p=l._(m),g=a("3fe68e88"),h=l._(g),v=a("fd7ca954"),b=a("4c1838f3"),j=l._(b);let{Item:x}=h.default,y=s.styled.div`
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
`;var P=a("5e00c259");let S=(0,s.styled)(e=>{let{items:t,wrapperStyle:a,...l}=e,r=(0,s.useSelectedRoutes)(),d=(0,s.useNavigate)(),[o]=(0,s.useSearchParams)(),n=(0,u.useMemo)(()=>{if(t)return t;let e=[];return null==r||r.forEach((t,a)=>{var l,i,s,d,o,n,u,c;(null==t?void 0:null===(l=t.route)||void 0===l?void 0:l.name)&&((0,j.default)(null==t?void 0:null===(i=t.route)||void 0===i?void 0:i.name)?e.push({key:t.pathnameBase,title:p.default.get(null==t?void 0:null===(s=t.route)||void 0===s?void 0:s.name),href:a+1!==r.length?(null==t?void 0:null===(d=t.route)||void 0===d?void 0:d.navPath)||(null==t?void 0:null===(o=t.route)||void 0===o?void 0:o.path):void 0}):e.push({key:t.pathnameBase,title:null==t?void 0:null===(n=t.route)||void 0===n?void 0:n.name,href:a+1!==r.length?(null==t?void 0:null===(u=t.route)||void 0===u?void 0:u.navPath)||(null==t?void 0:null===(c=t.route)||void 0===c?void 0:c.path):void 0}));}),e;},[r]),c=e=>{d((0,v.urlPathWithQuery)(e,[],o));};return(0,i.jsx)(y,{style:{...a},children:(0,i.jsx)(h.default,{...l,children:n.map(e=>(0,i.jsx)(x,{children:e.href?(0,i.jsx)("a",{className:"link-breadcrumb-item",onClick:()=>c(e.href),children:e.title}):(0,i.jsx)("span",{className:"span-breadcrumb-item",children:e.title})},e.key))})});})`
  &.breadcrumb {
    margin-bottom: 10px;
  }
`,C=s.styled.div`
  position: relative;
  padding: 0 var(--padding-lg) var(--padding-lg);
  &.layout-with-breadcrumb {
    padding-top: 12px;
  }
  &.layout-without-breadcrumb {
    padding-top: 24px;
  }
`,_=({children:e,breadcrumb:t=!1,loading:a,title:l,goBack:r,showBack:s,titleProps:d,className:n,...u})=>{if(a)return(0,i.jsx)(o.default,{active:!0});let c=!!t;return(0,i.jsxs)(C,{...u,className:(0,f.default)([{"layout-without-breadcrumb":!c},{"layout-with-breadcrumb":c},n]),children:[!1!==t&&(0,i.jsx)(S,{className:"breadcrumb",...t}),(l||(null==d?void 0:d.title))&&(0,i.jsx)(P.Title,{level:"page",title:l,goBackCb:r,showBack:s,...d}),e]});};},"406466c0":function(e,t,a){a.d(t,"__esModule",{value:!0}),a.e(t,{default:function(){return ea;}});var l=a("777fffbe"),r=a("852bbaa9"),i=a("32b7a2cf"),s=a("24c8531b"),d=l._(s),o=a("0a75b646"),n=l._(o),u=a("30f4392e"),c=a("df7e9d64"),f=a("f02131d0"),m=l._(f),p=a("8f891262"),g=l._(p),h=a("c60e02e5"),v=l._(h),b=a("fc1e2430"),j=l._(b),x=a("4d0e37ae"),y=r._(x),P=a("15f2f3a2"),S=l._(P),C=a("249809ea"),_=l._(C),N=a("22ceee98"),E=a("5b5ed4a9");let w=["useSearchUserInfo","getSearchUserInfo"],T=e=>{let{queryKeyword:t}=e??{};return(0,E.useQuery)({queryKey:[...w,t],queryFn:async()=>{if(!t)return[];let{result:e}=await (0,N.fuzzySearchAccounts)({queryStr:t});return Array.isArray(e)?e:[];},enabled:!!t});};var k=a("b916d24f"),R=l._(k),M=a("1e70bad6"),I=l._(M),z=a("fc1f4356");let O=e=>{let{loading:t,value:a,onSearch:l,configOptions:r,...s}=e,{delay:d=500}=r??{},o=(0,z.debounce)(e=>null==l?void 0:l(e),d);return(0,i.jsx)(j.default,{value:a,loading:t,allowClear:!0,showSearch:!0,filterOption:!1,onSearch:o,dropdownRender:e=>(0,i.jsx)(I.default,{spinning:t,children:e}),notFoundContent:(0,i.jsx)(R.default,{image:R.default.PRESENTED_IMAGE_SIMPLE,description:m.default.get({id:"spg.components.SelectUserModal.SearchSelect.NoData",dm:"\u6682\u65E0\u6570\u636E"})}),placeholder:m.default.get({id:"spg.components.SelectUserModal.SearchSelect.PleaseEnter",dm:"\u8BF7\u8F93\u5165"}),...s});},U=e=>{let{showProfilePicture:t}=(0,u.useEnv)(),{className:a,style:l,defaultValue:r,value:s,disabledOptions:d=[],...o}=e,n=r??s,[c,f]=(0,y.useState)(),{isFetching:m,data:p=[]}=T({queryKeyword:c}),g=(0,y.useMemo)(()=>null==p?void 0:p.map(e=>{let{employeeCardPic:a=`https://work.alipay-corp.com/photo/${e.workNo}.80x80.jpg`,nickName:l="",realName:r="",workNo:s="",account:o=""}=e;return{...e,label:(0,i.jsxs)(_.default,{children:[t&&(0,i.jsx)(S.default,{src:a}),(0,i.jsxs)(_.default,{direction:"vertical",size:0,children:[(0,i.jsx)("span",{style:{color:"#000000d9",fontSize:"12px",fontWeight:400},children:`${l}(${r})`}),(0,i.jsx)("span",{style:{color:"#000000a6",fontSize:"12px"},children:o})]})]}),displayLabel:(0,i.jsxs)(_.default,{children:[t&&(0,i.jsx)(S.default,{src:a}),(0,i.jsx)(_.default,{direction:"horizontal",size:0,children:(0,i.jsx)("span",{style:{color:"#000000d9",fontSize:"12px",fontWeight:400},children:`${l}(${r} ${o})`})})]}),value:s,disabled:d.includes(s)};}),[p,d]);return(0,y.useEffect)(()=>{n&&"string"==typeof n&&f(n);},[n]),(0,i.jsx)(O,{...o,defaultValue:r,value:s,dropdownMatchSelectWidth:!0,loading:m,options:g,optionLabelProp:"displayLabel",onSearch:f,configOptions:{delay:1e3}});},A=y.default.memo(e=>(0,i.jsx)(U,{...e}));var $=e=>{let{title:t,open:a,onOk:l,onCancel:r}=e,[s]=g.default.useForm();return(0,i.jsx)(v.default,{title:t||m.default.get({id:"spg.components.SelectUserModal.AddMembers",dm:"\u6DFB\u52A0\u6210\u5458"}),open:a,onOk:()=>{s.validateFields().then(e=>{l({...e}),s.resetFields();});},onCancel:r,width:800,children:(0,i.jsxs)(g.default,{form:s,preserve:!1,children:[(0,i.jsx)(g.default.Item,{label:m.default.get({id:"spg.components.SelectUserModal.User",dm:"\u7528\u6237"}),name:"userNos",rules:[{required:!0}],children:(0,i.jsx)(A,{mode:"multiple"})}),(0,i.jsx)(g.default.Item,{label:m.default.get({id:"spg.components.SelectUserModal.Role",dm:"\u89D2\u8272"}),name:"roleType",rules:[{required:!0}],initialValue:"MEMBER",children:(0,i.jsx)(j.default,{style:{width:180},options:[{label:"MEMBER",value:"MEMBER"},{label:"OWNER",value:"OWNER"}]})})]})});},q=a("52f7ae4a"),B=a("d5b565e0"),F=l._(B),L=a("673e678e"),W=l._(L),D=a("2b798761"),J=l._(D),Q=a("c7d6ff82"),K=l._(Q),V=a("3834a44f"),G=a("80411155"),H=l._(G),X=a("c7f4f8e9"),Y=l._(X);let Z=(0,E.styled)(Y.default)`
  flex: 1;
  overflow: scroll;

  .ant-table-thead {
    position: sticky;
    top: 0;
    z-index: 1;
  }

  .ant-table-tbody > tr > td {
    padding: 8px;
  }

  .ant-table-pagination {
    position: sticky;
    bottom: 0;
    z-index: 1;
    background: #fff;
    padding: 16px 0;
    margin: 0;
  }
`,ee=(0,E.styled)(H.default)`
  height: calc(100vh - 164px);

  .ant-card-body,
  .ant-spin-nested-loading,
  .ant-spin-container {
    height: 100%;
  }

  .ant-spin-container {
    display: flex;
    flex-direction: column;
  }
`,et=m.default.get({id:"spg.ProjectConfig.Permission.OwnerOrSuperTubeOnly",dm:"\u4EC5Owner\u6216\u8D85\u7BA1\u53EF\u64CD\u4F5C"});var ea=()=>{var e;let t=Number((0,V.getProjectId)()),{showProfilePicture:a}=(0,u.useEnv)(),{userInfo:l}=(0,c.useUserInfo)({resourceTag:"PROJECT",resourceId:(0,V.getProjectId)()}),[r]=(null==l?void 0:l.permissionList)||[],s=null==l?void 0:null===(e=l.permissionList)||void 0===e?void 0:e.some(e=>["SUPER","OWNER"].includes(e.accountRoleInfo.roleName)),[o,f]=(0,y.useState)(!1),[p,g]=(0,y.useState)(""),[h,v]=(0,y.useState)(""),[b,x]=(0,y.useState)({pageSize:10,current:1,total:0}),{isFetching:P,refetch:S,data:C={results:[],total:0,pageIdx:1,pageSize:10}}=(0,E.useQuery)({queryFn:async()=>{let e=await (0,q.getPermissionList)({resourceTag:"PROJECT",resourceId:t,queryStr:p,roleType:h,page:b.current,size:b.pageSize});if(e.success&&e.result)return e.result;},queryKey:["getPermissionList",t,p,h,b],enabled:!1,retry:!1});(0,y.useEffect)(()=>{S();},[t,p,h,b]);let{run:_,loading:N}=(0,E.useRequest)(q.create,{manual:!0,formatResult:e=>(e.success&&(J.default.success(m.default.get({id:"spg.ProjectConfig.Permission.AddedSuccessfully",dm:"\u6DFB\u52A0\u6210\u529F"})),f(!1),S()),e)}),{run:w,loading:T}=(0,E.useRequest)(q.deleteUsingDELETE,{manual:!0,formatResult:e=>(e.success&&(J.default.success(m.default.get({id:"spg.ProjectConfig.Permission.RemovalSucceeded",dm:"\u79FB\u9664\u6210\u529F"})),S()),e)}),{run:k,loading:R}=(0,E.useRequest)(q.update,{manual:!0,formatResult:e=>(e.success&&(J.default.success(m.default.get({id:"spg.ProjectConfig.Permission.UpdateSucceeded",dm:"\u66F4\u65B0\u6210\u529F"})),S()),e)}),M=[{title:"ID",key:"userName",render:(e,t)=>(0,i.jsxs)("div",{children:[a&&(0,i.jsx)("img",{src:`https://work.alipay-corp.com/photo/${t.userNo}.80x80.jpg`,style:{marginRight:5,borderRadius:"50%",width:26,height:26}}),(0,i.jsx)("span",{children:t.userName||t.userNo})]})},{title:m.default.get({id:"spg.ProjectConfig.Permission.Permissions",dm:"\u6743\u9650"}),key:"roleName",render:(e,t)=>{var a;return(0,i.jsx)(j.default,{style:{width:200},disabled:!s,options:[{label:m.default.get({id:"spg.ProjectConfig.Permission.ProjectAdministratorOwner",dm:"\u9879\u76EE\u7BA1\u7406\u5458\uFF08Owner\uFF09"}),value:"OWNER"},{label:m.default.get({id:"spg.ProjectConfig.Permission.ProjectMember",dm:"\u9879\u76EE\u6210\u5458\uFF08Member\uFF09"}),value:"MEMBER"}],value:null===(a=t.accountRoleInfo)||void 0===a?void 0:a.roleName,onChange:e=>{k({id:t.id},{id:t.id,roleType:e,resourceIds:[t.resourceId],resourceTag:t.resourceTag,userNos:[t.userNo]});}});}},{title:m.default.get({id:"spg.ProjectConfig.Permission.Operation",dm:"\u64CD\u4F5C"}),width:120,render:(e,t)=>(0,i.jsx)(K.default,{title:`\u{786E}\u{5B9A}\u{79FB}\u{9664} ${t.userName||t.userNo} \u{5417}\u{FF1F}`,okText:m.default.get({id:"spg.ProjectConfig.Permission.Determine",dm:"\u786E\u5B9A"}),cancelText:m.default.get({id:"spg.ProjectConfig.Permission.Cancel",dm:"\u53D6\u6D88"}),onConfirm:()=>{w({id:t.id},{id:t.id,resourceIds:[t.resourceId],resourceTag:t.resourceTag,userNos:[t.userNo]});},children:(null==r?void 0:r.userNo)===t.userNo?(0,i.jsx)(F.default,{type:"link",children:m.default.get({id:"spg.ProjectConfig.Permission.Exit",dm:"\u9000\u51FA"})}):(0,i.jsx)(n.default,{disabled:!s,tip:s?"":et,type:"link",children:m.default.get({id:"spg.ProjectConfig.Permission.Remove",dm:"\u79FB\u9664"})})})}];return(0,i.jsxs)(d.default,{titleProps:{title:m.default.get({id:"spg.ProjectConfig.Permission.Permissions",dm:"\u6743\u9650"})},children:[(0,i.jsx)(ee,{children:(0,i.jsxs)(I.default,{spinning:P||N||T||R,children:[(0,i.jsxs)("div",{style:{display:"flex",alignItems:"center",justifyContent:"space-between",marginBottom:16},children:[(0,i.jsx)("div",{children:m.default.get({id:"spg.ProjectConfig.Permission.TotalOfDatatotalPeople",dm:"\u5171 {dataTotal} \u4EBA"},{dataTotal:C.total})}),(0,i.jsxs)("div",{children:[(0,i.jsx)("span",{children:m.default.get({id:"spg.ProjectConfig.Permission.PermissionType",dm:"\u6743\u9650\u7C7B\u578B\uFF1A"})}),(0,i.jsx)(j.default,{style:{width:150},className:"mr16",value:h,placeholder:m.default.get({id:"spg.ProjectConfig.Permission.PleaseSelectAPermissionType",dm:"\u8BF7\u9009\u62E9\u6743\u9650\u7C7B\u578B"}),disabled:!1,options:[{label:m.default.get({id:"spg.ProjectConfig.Permission.All",dm:"\u5168\u90E8"}),value:""},{label:m.default.get({id:"spg.ProjectConfig.Permission.ProjectAdministrator",dm:"\u9879\u76EE\u7BA1\u7406\u5458"}),value:"OWNER"},{label:m.default.get({id:"spg.ProjectConfig.Permission.ProjectMembers",dm:"\u9879\u76EE\u6210\u5458"}),value:"MEMBER"}],onChange:v}),(0,i.jsx)(W.default.Search,{style:{width:255},className:"mr24",placeholder:m.default.get({id:"spg.ProjectConfig.Permission.EnterUserNameToSearch",dm:"\u8F93\u5165\u7528\u6237\u540D\u79F0\u641C\u7D22"}),allowClear:!0,onSearch:g}),(0,i.jsx)(n.default,{disabled:!s,tip:s?"":et,type:"primary",onClick:()=>{f(!0);},children:m.default.get({id:"spg.ProjectConfig.Permission.AddMembers",dm:"\u6DFB\u52A0\u6210\u5458"})})]})]}),(0,i.jsx)(Z,{showHeader:!0,columns:M,dataSource:C.results,onChange:e=>{x({pageSize:e.pageSize,current:e.current});},pagination:{position:["bottomRight"],total:C.total,current:b.current,pageSize:b.pageSize,showSizeChanger:!0,showQuickJumper:!0}})]})}),(0,i.jsx)($,{open:o,onOk:e=>{_({resourceTag:"PROJECT",resourceIds:[t],roleType:e.roleType,userNos:e.userNos});},onCancel:()=>{f(!1);}})]});};},"5e00c259":function(e,t,a){"use strict";a.d(t,"__esModule",{value:!0}),a.d(t,"Title",{enumerable:!0,get:function(){return d;}});var l=a("32b7a2cf"),r=a("5b5ed4a9");a("4d0e37ae");var i=a("fc1f4356");let s=r.styled.div`
  .header {
    color: var(--dark-shade-85);
    font-weight: var(--font-medium);
    font-size: ${({$level:e})=>"page"===e?"var(--h1-font-size)":"var(--h3-font-size)"};
    line-height: ${({$level:e})=>"page"===e?"28px":"24px"};
  }
`,d=({className:e,style:t,level:a="page",title:d,titleExtra:o,showBack:n=!1,goBackCb:u,children:c})=>{let f=(0,r.useNavigate)();return(0,l.jsxs)(s,{$level:a,className:e,style:t,children:[(0,l.jsxs)("div",{className:"flex-row mb16",children:[(0,l.jsxs)("div",{className:"header",children:[n&&(0,l.jsx)(r.Icon,{className:"icon mr8 pointer",icon:"ant-design:arrow-left-outlined",onClick:()=>(0,i.isFunction)(u)?u():f(-1)}),d]}),(0,l.jsx)("div",{children:o})]}),(0,l.jsx)("div",{children:c})]});};},c5f39a1b:function(e,t,a){"use strict";a.d(t,"__esModule",{value:!0}),a.d(t,"default",{enumerable:!0,get:function(){return l;}});var l=a("777fffbe")._(a("85d9e535")).default;}}]);
