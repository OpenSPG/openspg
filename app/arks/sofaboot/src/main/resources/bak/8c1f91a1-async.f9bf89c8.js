(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["8c1f91a1"],{"0b69d691":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{getEntityDetail:function(){return s;},getEnumValues:function(){return d;},getLlmSelect:function(){return r;},getOneHopGraph:function(){return i;},getSampleData:function(){return l;},search:function(){return o;}});var a=n("05ecbb5b");async function s(e,t){return(0,a.request)("/v1/datas/getEntityDetail",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}async function d(e,t){let{name:n}=e;return(0,a.request)(`/v1/datas/getEnumValues/${n}`,{method:"GET",params:{...e},...t||{}});}async function r(e,t){return(0,a.request)("/v1/datas/getLlmSelect",{method:"GET",params:{...e},...t||{}});}async function i(e,t){return(0,a.request)("/v1/datas/getOneHopGraph",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}async function l(e,t){return(0,a.request)("/v1/datas/getSampleData",{method:"GET",params:{...e},...t||{}});}async function o(e,t){return(0,a.request)("/v1/datas/search",{method:"GET",params:{...e},...t||{}});}},"218e9afa":function(e,t,n){n.d(t,"__esModule",{value:!0}),n.e(t,{default:function(){return V;}});var a=n("777fffbe"),s=n("852bbaa9"),d=n("32b7a2cf"),r=n("5e00c259"),i=n("6e1f6d03"),l=n("df7e9d64"),o=n("f02131d0"),u=a._(o),c=n("c72fc2b9"),f=n("5b5ed4a9"),g=n("d5b565e0"),p=a._(g),m=n("673e678e"),h=a._(m),b=n("249809ea"),T=a._(b),x=n("c7f4f8e9"),k=a._(x),v=n("4d0e37ae"),y=s._(v),w=n("3834a44f"),j=n("483403ba"),I=n("490840b0"),N=a._(I),S=n("7603c0eb"),E=n("2b798761"),M=a._(E),_=n("c7d6ff82"),K=a._(_);let C=({taskId:e,taskFinished:t,onSuccess:n,children:a})=>{let[s,r]=(0,y.useState)(!1),i=t?u.default.get({id:"spg.KnowledgeTask.components.DeleteTaskBtn.DeletingTheDocumentWillAlso",dm:"\u5220\u9664\u6587\u6863\u540C\u65F6\u5C06\u5220\u9664\u5DF2\u62BD\u53D6\u7684\u77E5\u8BC6\uFF0C\u786E\u5B9A\u5220\u9664\u5417\uFF1F"}):u.default.get({id:"spg.KnowledgeTask.components.DeleteTaskBtn.AreYouSureYouWant",dm:"\u786E\u5B9A\u5220\u9664\u8BE5\u6587\u6863\u5417\uFF1F"}),l=async()=>{r(!0),(0,c.deleteUsingGET)({id:e}).then(e=>{e.success&&(M.default.success(u.default.get({id:"spg.KnowledgeTask.components.DeleteTaskBtn.DeletedSuccessfully",dm:"\u5220\u9664\u6210\u529F"})),null==n||n());}).finally(()=>{r(!1);});};return(0,d.jsx)(K.default,{title:i,onConfirm:l,disabled:s,cancelButtonProps:{disabled:s},okText:u.default.get({id:"spg.KnowledgeTask.components.DeleteTaskBtn.Confirm",dm:"\u786E\u8BA4"}),cancelText:u.default.get({id:"spg.KnowledgeTask.components.DeleteTaskBtn.Cancel",dm:"\u53D6\u6D88"}),children:(0,d.jsx)(p.default,{type:"link",loading:s,children:a||u.default.get({id:"spg.KnowledgeTask.components.DeleteTaskBtn.Delete",dm:"\u5220\u9664"})})});};var R=n("7648835d"),D=a._(R),O=n("6d1765ea");let{Paragraph:G}=a._(O).default,q=(0,f.styled)(D.default)`
  .ant-progress-circle {
    animation: ${({$isProcess:e})=>e?"rotate 2s linear infinite":"null"};
  }
  .ant-steps-item-content {
    .ant-steps-item-description {
      word-wrap: break-word;
      word-break: break-all;
      white-space: break-spaces;
      .ant-typography {
        margin-bottom: 0;
      }
    }
  }
  .ant-steps-item-error {
    .ant-typography {
      color: var(--error-color);
    }
  }
`,P=({data:e,style:t})=>{var n;let a=(null==e?void 0:null===(n=e.sort)||void 0===n?void 0:n.call(e,(e,t)=>e.index-t.index))||[],s=!!a.find(e=>"ERROR"===e.status),r=!!a.find(e=>"RUNNING"===e.status);return(0,d.jsx)(q,{direction:"vertical",size:"small",status:s?"error":void 0,$isProcess:r,percent:r?60:void 0,current:(()=>{let e=a.findIndex(e=>"FINISH"!==e.status);return -1!==e?e:a.length+1;})(),style:t,items:a.map(e=>({title:e.name,description:(0,d.jsx)(G,{ellipsis:"RUNNING"!==e.status&&"ERROR"!==e.status&&{rows:4,expandable:!0,symbol:u.default.get({id:"spg.DialogBox.DialogContainer.ParseProcess.Expand",dm:"\u5C55\u5F00"})},children:e.traceLog})}))});};var B=n("51196c4f"),A=n("2ed4c134"),$=a._(A),F=n("1e70bad6"),L=a._(F),U=n("b2eda3a1"),z=n("1de95a03"),W=a._(z);let H=({taskId:e,jobId:t,drawerProps:n})=>{var a;let{onClose:s}=n??{},[r,l]=(0,y.useState)(!1),{data:o,isFetching:c}=(0,f.useQuery)(["buildLog",e],()=>(0,B.builderQuery)({request:{id:e,jobId:t}}),{enabled:r,refetchInterval:e=>{var t;let n=(null==e?void 0:null===(t=e.result)||void 0===t?void 0:t.resultMessage)||"";try{let e=(0,U.json2Object)(n);if((0,W.default)(e)&&e.some(e=>e.status===i.TaskStatus.RUNNING))return 3e3;}catch(e){console.error("Error parsing result message:",e);}return!1;},refetchIntervalInBackground:!0}),g=(null==o?void 0:null===(a=o.result)||void 0===a?void 0:a.resultMessage)||"",m=(0,U.json2Object)(g);return(0,d.jsxs)(d.Fragment,{children:[(0,d.jsx)(p.default,{type:"link",onClick:()=>l(!0),children:u.default.get({id:"spg.KnowledgeTask.components.TaskLogBtn.Log",dm:"\u65E5\u5FD7"})}),(0,d.jsx)($.default,{title:u.default.get({id:"spg.KnowledgeTask.components.TaskLogBtn.Log",dm:"\u65E5\u5FD7"}),open:r,closable:!0,onClose:(...e)=>{l(!1),null==s||s(...e);},width:700,bodyStyle:{background:"var(--pale-blue)"},children:(0,d.jsx)(L.default,{spinning:c,children:(0,W.default)(m)&&(0,d.jsx)(P,{data:m.sort((e,t)=>e.index-t.index)})})})]});},Q=(0,f.styled)(T.default)`
  .ant-btn {
    padding-left: 0;
    padding-right: 0;
  }
`;function V(){let{userInfo:e}=(0,l.useUserInfo)(),[t]=e.permissionList,n=String(e.info.account),a=(0,w.useProjectId)(),[s,o]=(0,y.useState)({start:1,limit:10}),{start:g,limit:m}=s,{data:b,isFetching:x,refetch:v}=(0,f.useQuery)(["knowledgeTaskList",s,a],async()=>{let e=await (0,c.list)({...s,projectId:Number(a)});return(null==e?void 0:e.result)||{};}),{data:I=[],total:E=0}=b||{},M=[{title:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.TaskName",dm:"\u77E5\u8BC6\u540D\u79F0"}),dataIndex:"jobName",render:e=>(0,d.jsx)(j.EllipsisTextTip,{text:e})},{title:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.FileType",dm:"\u6587\u4EF6\u7C7B\u578B"}),dataIndex:"dataSourceType",render:e=>e||"-"},{title:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.ImportType",dm:"\u5BFC\u5165\u7C7B\u578B"}),dataIndex:"lifeCycle",render:e=>e},{title:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.CreateOwner",dm:"\u521B\u5EFAOwner"}),dataIndex:"createUser",width:144},{title:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.CreationTime",dm:"\u66F4\u65B0\u65F6\u95F4"}),dataIndex:"gmtModified",width:180},{title:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.TaskStatus",dm:"\u4EFB\u52A1\u72B6\u6001"}),dataIndex:"status",width:148,render:e=>(0,d.jsx)(N.default,{status:e})},{title:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.Operation",dm:"\u64CD\u4F5C"}),dataIndex:"id",render:(e,s)=>{var r;let{status:l,createUser:o,taskId:c}=s,g=(null==t?void 0:null===(r=t.accountRoleInfo)||void 0===r?void 0:r.roleName)==="SUPER",m=n===o,h=[i.TaskStatus.WAIT,i.TaskStatus.ERROR,i.TaskStatus.TERMINATE,i.TaskStatus.PENDING].includes(l)&&m,b=m||g,T=i.TaskStatus.FINISH===l;return i.TaskStatus.PENDING,(0,d.jsxs)(Q,{size:8,children:[h&&(0,d.jsx)(p.default,{type:"link",onClick:()=>f.history.push(`/knowledgeModeling/knowledgeTask/edit?projectId=${a}&jobId=${s.id}`,{editTaskInfo:s}),children:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.Edit",dm:"\u7F16\u8F91"})}),(0,d.jsx)(p.default,{type:"link",onClick:()=>{f.history.push(`/knowledgeModeling/knowledgeTask/details?projectId=${a}&jobId=${e}`);},children:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.Details",dm:"\u8BE6\u60C5"})}),c&&(0,d.jsx)(H,{taskId:c,jobId:e,drawerProps:{onClose:()=>v()}}),b&&(0,d.jsx)(C,{taskId:e,taskFinished:T,onSuccess:v})]});}}];return(0,d.jsxs)(S.TopBoxDiv,{children:[(0,d.jsx)(r.Title,{level:"page",title:u.default.get({id:"spg.KnowledgeModeling.KnowledgeModel.KnowledgeTask",dm:"\u77E5\u8BC6\u4EFB\u52A1"}),titleExtra:(0,d.jsxs)(T.default,{children:[(0,d.jsx)(h.default.Search,{placeholder:u.default.get({id:"spg.KnowledgeModeling.KnowledgeTask.PleaseEnterATaskName",dm:"\u8BF7\u8F93\u5165\u4EFB\u52A1\u540D\u79F0"}),allowClear:!0,onSearch:e=>{e===s.keyword?v():o({...s,start:1,keyword:e});}}),(0,d.jsx)(p.default,{type:"primary",onClick:()=>{f.history.push(`/knowledgeModeling/knowledgeTask/edit?projectId=${a}`);},children:u.default.get({id:"spg.KnowledgeModeling.KnowledgeModel.CreateTask",dm:"\u521B\u5EFA\u4EFB\u52A1"})})]})}),(0,d.jsx)(S.BaseBoxDiv,{padding:"24px",children:(0,d.jsx)(k.default,{tableLayout:"fixed",columns:M,dataSource:I,rowKey:"id",pagination:{current:g,pageSize:m,total:E,showSizeChanger:!0,onChange:(e,t)=>{o({...s,start:e,limit:t});}},loading:x})})]});}},"490840b0":function(e,t,n){n.d(t,"__esModule",{value:!0}),n.e(t,{default:function(){return h;}});var a=n("777fffbe"),s=n("852bbaa9"),d=n("32b7a2cf"),r=n("6e1f6d03"),i=n("5b5ed4a9"),l=n("9e0828f5"),o=a._(l),u=n("02b696d0"),c=s._(u),f=n("0b69d691"),g=n("05ecbb5b"),p=n("93b3369a");let m=()=>{let e={name:"BuilderJobStatus"},t=(0,g.useQuery)({queryKey:["KnowledgeModeling/getEnumValues",e],queryFn:async()=>{let{success:t,result:n}=await (0,f.getEnumValues)(e);return t&&Array.isArray(n)?n:Promise.reject(n);},cacheTime:1/0,staleTime:1/0}),n=t.data,a=(0,c.useMemo)(()=>(0,p.keyBy)(n,"name"),[n]);return{...t,data:a};},h=e=>{var t;let{status:n=""}=e,{data:a}=m(),s=(0,i.getLocale)(),{name:l,text:u}=a[n]??{},c=(null===(t=r.TaskStatusMap[l??""])||void 0===t?void 0:t.status)??{};return(0,d.jsx)(o.default,{status:c||"default",text:("zh-CN"===s?u:l)||""});};},"51196c4f":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{builderQuery:function(){return s;},list:function(){return d;},mark:function(){return r;},unMark:function(){return i;}});var a=n("05ecbb5b");async function s(e,t){return(0,a.request)("/public/v1/reasoner/task/builder/query",{method:"GET",params:{...e,request:void 0,...e.request},...t||{}});}async function d(e,t){return(0,a.request)("/public/v1/reasoner/task/list",{method:"GET",params:{...e,request:void 0,...e.request},...t||{}});}async function r(e,t){return(0,a.request)("/public/v1/reasoner/task/mark",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}async function i(e,t){return(0,a.request)("/public/v1/reasoner/task/unmark",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}},"5e00c259":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.d(t,"Title",{enumerable:!0,get:function(){return i;}});var a=n("32b7a2cf"),s=n("5b5ed4a9");n("4d0e37ae");var d=n("fc1f4356");let r=s.styled.div`
  .header {
    color: var(--dark-shade-85);
    font-weight: var(--font-medium);
    font-size: ${({$level:e})=>"page"===e?"var(--h1-font-size)":"var(--h3-font-size)"};
    line-height: ${({$level:e})=>"page"===e?"28px":"24px"};
  }
`,i=({className:e,style:t,level:n="page",title:i,titleExtra:l,showBack:o=!1,goBackCb:u,children:c})=>{let f=(0,s.useNavigate)();return(0,a.jsxs)(r,{$level:n,className:e,style:t,children:[(0,a.jsxs)("div",{className:"flex-row mb16",children:[(0,a.jsxs)("div",{className:"header",children:[o&&(0,a.jsx)(s.Icon,{className:"icon mr8 pointer",icon:"ant-design:arrow-left-outlined",onClick:()=>(0,d.isFunction)(u)?u():f(-1)}),i]}),(0,a.jsx)("div",{children:l})]}),(0,a.jsx)("div",{children:c})]});};},"6e1f6d03":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{TaskStatus:function(){return a;},TaskStatusMap:function(){return r;}});var a,s,d=n("777fffbe")._(n("f02131d0"));(s=a||(a={})).WAIT="WAIT",s.RUNNING="RUNNING",s.ERROR="ERROR",s.TERMINATE="TERMINATE",s.PENDING="PENDING",s.FINISH="FINISH";let r={WAIT:{status:"default",text:d.default.get({id:"spg.src.constants.knowledgeTask.Waiting",dm:"\u7B49\u5F85\u4E2D"})},RUNNING:{status:"processing",text:d.default.get({id:"spg.src.constants.knowledgeTask.InExecution",dm:"\u6267\u884C\u4E2D"})},TERMINATE:{status:"warning",text:d.default.get({id:"spg.src.constants.knowledgeTask.Termination",dm:"\u7EC8\u6B62"})},ERROR:{status:"error",text:d.default.get({id:"spg.src.constants.knowledgeTask.Exception",dm:"\u5F02\u5E38"})},PENDING:{status:"default",text:d.default.get({id:"spg.src.constants.knowledgeTask.ToBeImported",dm:"\u5F85\u5BFC\u5165"})},FINISH:{status:"success",text:d.default.get({id:"spg.src.constants.knowledgeTask.Finish",dm:"\u5B8C\u6210"})}};},"7603c0eb":function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{BaseBoxDiv:function(){return i;},CardWithStyle:function(){return o;},ContentDiv:function(){return l;},KgsSearchStyle:function(){return u;},TopBoxDiv:function(){return r;}});var a=n("777fffbe"),s=n("5b5ed4a9"),d=a._(n("80411155"));let r=s.styled.div`
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
`,i=s.styled.div`
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0px 0px 1px 0px #00000014, 0px 1px 2px 0px #190f0f12,
    0px 2px 4px 0px #0000000d;
  min-height: 300px;
  flex: 1;
  padding: ${e=>e.padding||"0px"};
  overflow: auto;
`,l=s.styled.div`
  height: calc(100vh - 48px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;
`,o=(0,s.styled)(d.default)`
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;

  .ant-card-body {
    overflow: hidden;
    padding: 0;
    flex: 1;
  }
`,u=(0,s.createGlobalStyle)`
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
`;},b2eda3a1:function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{json2Object:function(){return d;},json2String:function(){return r;},renderWithDefaultText:function(){return i;}});var a=n("fc1f4356"),s=n("02b696d0");function d(e,t={}){if((0,a.isNil)(e))return t;try{if((0,a.isObject)(e))return e;if((0,a.isString)(e)){let t=e.replace(/\r/gi,"").replace(/\n/gi,""),n=JSON.parse(t);if((0,a.isObject)(n))return n;}throw Error();}catch(n){return console.warn(`${e} is not a valid Object`),t;}}function r(e){return"string"==typeof e?e:JSON.stringify(e);}function i(e){return(0,s.isValidElement)(e)?e:(0,a.isNil)(e)?"-":r(e)||"-";}},c72fc2b9:function(e,t,n){"use strict";n.d(t,"__esModule",{value:!0}),n.e(t,{deleteUsingGET:function(){return s;},getById:function(){return d;},list:function(){return r;},schemaDiff:function(){return i;},splitPreview:function(){return l;},submit:function(){return o;}});var a=n("05ecbb5b");async function s(e,t){return(0,a.request)("/public/v1/builder/job/delete",{method:"GET",params:{...e},...t||{}});}async function d(e,t){return(0,a.request)("/public/v1/builder/job/get",{method:"GET",params:{...e},...t||{}});}async function r(e,t){return(0,a.request)("/public/v1/builder/job/list",{method:"GET",params:{...e},...t||{}});}async function i(e,t){return(0,a.request)("/public/v1/builder/job/schema/diff",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}async function l(e,t){return(0,a.request)("/public/v1/builder/job/split/preview",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}async function o(e,t){return(0,a.request)("/public/v1/builder/job/submit",{method:"POST",headers:{"Content-Type":"application/json"},data:e,...t||{}});}}}]);
