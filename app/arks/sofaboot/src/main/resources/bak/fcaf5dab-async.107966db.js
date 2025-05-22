(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["fcaf5dab"],{"465d5e1b":function(e,t,a){"use strict";a.d(t,"__esModule",{value:!0}),a.d(t,"pwdCryptoSha256",{enumerable:!0,get:function(){return r;}});var s=a("777fffbe")._(a("09064ba3"));let r=e=>s.default.SHA256(e+"OPENSPG").toString();},"5b9c9109":function(e,t,a){e.exports=`${a.publicPath}login-bg.ccffd4ea.png`;},"85ceaa92":function(e,t,a){e.exports=`${a.publicPath}logo-spg.0fb268ec.png`;},"952e8d5e":function(e,t,a){"use strict";a.d(t,"__esModule",{value:!0}),a.d(t,"useUserManager",{enumerable:!0,get:function(){return m;}});var s=a("777fffbe"),r=a("cd8b2a5f"),o=s._(a("f02131d0")),n=a("5b5ed4a9"),d=s._(a("2b798761")),l=a("4d0e37ae"),i=a("465d5e1b"),u=a("05ecbb5b");async function c(e){return(0,u.request)("/v1/accounts/list",{method:"GET",params:e});}async function f(e){return(0,u.request)("/v1/accounts",{method:"POST",data:e});}async function g(e){return(0,u.request)("/v1/accounts/updatePassword",{method:"POST",data:e});}async function p(e){return(0,u.request)(`/v1/accounts/${e}`,{method:"DELETE",headers:{"Content-Type":"application/json"}});}let m=e=>{let{page:t=1,size:a=10,searchUserKeyword:s="",initGetUserList:u=!0}=e||{},{isFetching:m,refetch:h,data:b={}}=(0,n.useQuery)({queryFn:async()=>{let e=await c({page:t,size:a,account:s});if(e.success&&e.result)return e.result;},queryKey:["getUserList",t,a,s],enabled:!1,retry:!1});(0,l.useEffect)(()=>{u&&h();},[]),(0,r.useUpdateEffect)(()=>{h();},[t,a,s]);let{run:x,loading:w}=(0,n.useRequest)(f,{manual:!0,formatResult:e=>{e.success&&(e.result||0)>0&&(d.default.success(o.default.get({id:"spg.GlobalConfig.hooks.userManager.SuccessfullyCreated",dm:"\u521B\u5EFA\u6210\u529F"})),h());}}),{run:y,loading:P}=(0,n.useRequest)(g,{manual:!0,formatResult:e=>{if(e.success&&(e.result||0)>0)return d.default.success(o.default.get({id:"spg.GlobalConfig.hooks.userManager.PasswordModifiedSuccessfully",dm:"\u5BC6\u7801\u4FEE\u6539\u6210\u529F"})),e.result;}}),{run:v,loading:j}=(0,n.useRequest)(p,{manual:!0,formatResult:e=>{e.success&&(d.default.success(o.default.get({id:"spg.GlobalConfig.hooks.userManager.DeletedSuccessfully",dm:"\u5220\u9664\u6210\u529F"})),h());}});return{userList:b,userListLoading:m,refreshUserList:h,createUser:e=>x({account:e.account,password:(0,i.pwdCryptoSha256)(e.password)}),creating:w,updatePassword:e=>y({workNo:e.workNo,password:(0,i.pwdCryptoSha256)(e.password),confirmPassword:(0,i.pwdCryptoSha256)(e.confirmPassword)}),pwdUpdating:P,deleteUser:v,deleting:j};};},e60546d9:function(e,t,a){a.d(t,"__esModule",{value:!0}),a.e(t,{default:function(){return J;}});var s=a("777fffbe"),r=a("852bbaa9"),o=a("32b7a2cf"),n=a("85ceaa92"),d=s._(n),l=a("f02131d0"),i=s._(l),u=a("5b5ed4a9"),c=a("d5b565e0"),f=s._(c),g=a("80411155"),p=s._(g),m=a("8f891262"),h=s._(m),b=a("673e678e"),x=s._(b),w=a("2b798761"),y=s._(w),P=a("6d1765ea"),v=s._(P),j=a("4d0e37ae"),S=r._(j),_=a("1043743b"),k=s._(_),C=a("02b696d0"),R=r._(C),L={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M832 464h-68V240c0-70.7-57.3-128-128-128H388c-70.7 0-128 57.3-128 128v224h-68c-17.7 0-32 14.3-32 32v384c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V496c0-17.7-14.3-32-32-32zM332 240c0-30.9 25.1-56 56-56h248c30.9 0 56 25.1 56 56v224H332V240zm460 600H232V536h560v304zM484 701v53c0 4.4 3.6 8 8 8h40c4.4 0 8-3.6 8-8v-53a48.01 48.01 0 10-56 0z"}}]},name:"lock",theme:"outlined"},N=a("833883d6"),E=s._(N),T=R.forwardRef(function(e,t){return R.createElement(E.default,(0,k.default)((0,k.default)({},e),{},{ref:t,icon:L}));}),q={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M858.5 763.6a374 374 0 00-80.6-119.5 375.63 375.63 0 00-119.5-80.6c-.4-.2-.8-.3-1.2-.5C719.5 518 760 444.7 760 362c0-137-111-248-248-248S264 225 264 362c0 82.7 40.5 156 102.8 201.1-.4.2-.8.3-1.2.5-44.8 18.9-85 46-119.5 80.6a375.63 375.63 0 00-80.6 119.5A371.7 371.7 0 00136 901.8a8 8 0 008 8.2h60c4.4 0 7.9-3.5 8-7.8 2-77.2 33-149.5 87.8-204.3 56.7-56.7 132-87.9 212.2-87.9s155.5 31.2 212.2 87.9C779 752.7 810 825 812 902.2c.1 4.4 3.6 7.8 8 7.8h60a8 8 0 008-8.2c-1-47.8-10.9-94.3-29.5-138.2zM512 534c-45.9 0-89.1-17.9-121.6-50.4S340 407.9 340 362c0-45.9 17.9-89.1 50.4-121.6S466.1 190 512 190s89.1 17.9 121.6 50.4S684 316.1 684 362c0 45.9-17.9 89.1-50.4 121.6S557.9 534 512 534z"}}]},name:"user",theme:"outlined"},M=R.forwardRef(function(e,t){return R.createElement(E.default,(0,k.default)((0,k.default)({},e),{},{ref:t,icon:q}));}),z=a("5b9c9109"),F=s._(z),A=a("c60e02e5"),G=s._(A);let I=u.styled.div`
  width: 100%;
  height: 100%;
  background-image: url(${F.default});
  background-size: cover;
  background-position: right bottom;

  .layout-header-container {
    padding: 15px 32px;
    border-bottom: 1px solid #ffffff14;

    & > img {
      height: 32px;
    }
  }

  .layout-center-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    position: absolute;
    left: 50%;
    top: 45%;
    transform: translate(-50%, -50%);

    .layout-title-first {
      margin-bottom: 16px;

      .layout-title-text-left {
        font-family: AlibabaPuHuiTiM;
        font-size: 36px;
        color: transparent;
        line-height: 48px;
        background-image: linear-gradient(180deg, #439bff -44%, #0077ff 89%);
        background-clip: text;
      }

      .layout-title-text-right {
        color: #fff;
      }
    }

    .layout-sub-title-text {
      margin-bottom: 40px;
      color: #fff;
      opacity: 0.45;
      font-size: 18px;
    }

    .login-form {
      width: 462px;

      .ant-card {
        background: #212a3f;

        .ant-card-body {
          padding: 24px 32px 0;
        }
      }

      .login-title {
        color: #ffffffe3;
        text-align: center;
        font-weight: 500;
        margin-bottom: 24px;
        margin-top: 12px;
      }

      .login-form-ipt-style {
        border: none;
        height: 40px;
        background: #ffffff15 !important;
        border-radius: 8px;

        .ant-input-prefix {
          margin-right: 10px;
        }

        & > input {
          background: transparent;
          color: #fff;

          &::placeholder {
            color: #ffffff42;
          }
        }

        .ant-input-password-icon {
          color: #ffffff42;
        }
      }

      .submit-btn {
        margin-top: 16px;
        button {
          width: 100%;
          height: 40px;
        }
      }
      .third-party-login {
        margin-bottom: 40px;

        .ant-form-item-control-input-content {
          text-align: center;

          .github-icon {
            font-size: 18px;
            color: #fff;
          }
        }
      }
    }
  }
`,O=(0,u.styled)(G.default)`
  .ant-modal-body {
    padding-top: 0;

    .reset-pwd-tip {
      width: calc(100% + 48px);
      height: 40px;
      margin-left: -24px;
      margin-bottom: 24px;
      padding: 0 24px;
      line-height: 40px;
      background: #fffbe6;
    }
  }
`;var U=a("22ceee98"),$=a("465d5e1b"),H=a("05ecbb5b"),B=a("7ac0ebee"),D=s._(B),V=a("952e8d5e");let K=e=>{let{open:t}=e,a=(0,u.useNavigate)(),[s,r]=(0,S.useState)(t);(0,S.useEffect)(()=>{t&&r(!0);},[t]);let{updatePassword:n,pwdUpdating:d}=(0,V.useUserManager)({initGetUserList:!1}),[l]=h.default.useForm();return(0,o.jsxs)(O,{open:s,title:i.default.get({id:"spg.pages.Login.ResetPwdComp.ResetPassword",dm:"\u91CD\u7F6E\u5BC6\u7801"}),closable:!1,maskClosable:!1,cancelButtonProps:{hidden:!0},okButtonProps:{loading:d},onOk:()=>{l.validateFields().then(async e=>{let{password:t,confirmPassword:s}=e;n({password:t,confirmPassword:s}).then(e=>{e&&(r(!1),a("/home"));});});},children:[(0,o.jsxs)("div",{className:"reset-pwd-tip",children:[(0,o.jsx)(D.default,{className:"mr8",style:{color:"#faad14"}}),(0,o.jsx)("span",{children:i.default.get({id:"spg.pages.Login.ResetPwdComp.PleaseResetThePasswordOtherwise",dm:"\u8BF7\u91CD\u8BBE\u5BC6\u7801\uFF0C\u5426\u5219\u65E0\u6CD5\u4F7F\u7528"})})]}),(0,o.jsxs)(h.default,{form:l,children:[(0,o.jsx)(h.default.Item,{name:"password",label:i.default.get({id:"spg.pages.Login.ResetPwdComp.NewPassword",dm:"\u65B0\u5BC6\u7801"}),rules:[{required:!0,message:i.default.get({id:"spg.pages.Login.ResetPwdComp.PleaseEnterANewPassword",dm:"\u8BF7\u8F93\u5165\u65B0\u5BC6\u7801\uFF01"})},{pattern:/^\S{8,20}$/,message:i.default.get({id:"spg.pages.Login.ResetPwdComp.ThePasswordIsDigitsIn",dm:"\u5BC6\u7801\u4E3A\u957F\u5EA6\u4E3A8-20\u4F4D\uFF0C\u4E0D\u80FD\u662F\u7A7A\u683C\uFF01"})}],hasFeedback:!0,children:(0,o.jsx)(x.default.Password,{})}),(0,o.jsx)(h.default.Item,{name:"confirmPassword",label:i.default.get({id:"spg.pages.Login.ResetPwdComp.ConfirmPassword",dm:"\u786E\u8BA4\u5BC6\u7801"}),dependencies:["password"],hasFeedback:!0,rules:[{required:!0,message:i.default.get({id:"spg.pages.Login.ResetPwdComp.PleaseEnterTheNewPassword",dm:"\u8BF7\u518D\u6B21\u8F93\u5165\u65B0\u5BC6\u7801\uFF01"})},{pattern:/^\S{8,20}$/,message:i.default.get({id:"spg.pages.Login.ResetPwdComp.ThePasswordIsDigitsIn",dm:"\u5BC6\u7801\u4E3A\u957F\u5EA6\u4E3A8-20\u4F4D\uFF0C\u4E0D\u80FD\u662F\u7A7A\u683C\uFF01"})},({getFieldValue:e})=>({validator:(t,a)=>a&&e("password")!==a?Promise.reject(Error(i.default.get({id:"spg.pages.Login.ResetPwdComp.ThePasswordsEnteredTwiceAre",dm:"\u4E24\u6B21\u8F93\u5165\u7684\u5BC6\u7801\u4E0D\u4E00\u81F4\uFF01"}))):Promise.resolve()})],children:(0,o.jsx)(x.default.Password,{})})]})]});};async function Q(){return(0,H.request)("/v1/accounts/oauth/github/url",{method:"GET"});}let{Title:W}=v.default;var J=()=>{let[e]=(0,u.useSearchParams)(),t="true"===e.get("needResetPwd"),[a,s]=(0,S.useState)(t),[r]=h.default.useForm(),n=(0,u.useNavigate)(),{run:l,loading:c}=(0,u.useRequest)(U.login,{manual:!0,formatResult:e=>{if(e.success){if(e.result)return y.default.success(i.default.get({id:"spg.pages.Login.LoginSucceeded",dm:"\u767B\u5F55\u6210\u529F"})),n("/home"),e;n("/login?needResetPwd=true"),s(!0);}return e;}}),{run:g,loading:m}=(0,u.useRequest)(Q,{manual:!0,formatResult:e=>(e.success&&"string"==typeof e.result&&e.result.startsWith("https://github.com/login")&&window.location.assign(e.result),e)});return(0,o.jsxs)(I,{children:[(0,o.jsx)("div",{className:"layout-header-container",children:(0,o.jsx)("img",{src:d.default})}),(0,o.jsxs)("div",{className:"layout-center-container",children:[(0,o.jsxs)(W,{className:"layout-title-first",children:[(0,o.jsx)("span",{className:"layout-title-text-left",children:"OpenSPG"}),(0,o.jsx)("span",{className:"layout-title-text-right",children:i.default.get({id:"spg.pages.Login.SemanticallyEnhancedEditableAtlasFramework",dm:"\xb7\u8BED\u4E49\u589E\u5F3A\u53EF\u7F16\u8F91\u56FE\u8C31\u6846\u67B6"})})]}),(0,o.jsx)("div",{className:"layout-sub-title-text",children:i.default.get({id:"spg.pages.Login.ANewGenerationOfEnterprise",dm:"\u65B0\u4E00\u4EE3\u4F01\u4E1A\u7EA7\u77E5\u8BC6\u56FE\u8C31\u5F15\u64CE\uFF0C\u5927\u6A21\u578B\u4E0E\u77E5\u8BC6\u56FE\u8C31\u53CC\u5411\u589E\u5F3A"})}),(0,o.jsx)(h.default,{form:r,className:"login-form",onFinish:e=>{let{password:t}=e,a=(0,$.pwdCryptoSha256)(t);l({account:e.account,password:a});},children:(0,o.jsxs)(p.default,{bordered:!1,children:[(0,o.jsx)(W,{level:4,className:"login-title",children:i.default.get({id:"spg.pages.Login.Login",dm:"\u767B\u5F55"})}),(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(h.default.Item,{name:"account",rules:[{required:!0,message:i.default.get({id:"spg.pages.Login.PleaseEnterTheAccountNumber",dm:"\u8BF7\u8F93\u5165\u8D26\u53F7"})}],children:(0,o.jsx)(x.default,{className:"login-form-ipt-style",prefix:(0,o.jsx)(M,{style:{color:"#fff"}}),placeholder:i.default.get({id:"spg.pages.Login.PleaseEnterTheAccountNumber",dm:"\u8BF7\u8F93\u5165\u8D26\u53F7"})})}),(0,o.jsx)(h.default.Item,{name:"password",rules:[{required:!0,message:i.default.get({id:"spg.pages.Login.PleaseEnterPassword",dm:"\u8BF7\u8F93\u5165\u5BC6\u7801"})}],children:(0,o.jsx)(x.default.Password,{className:"login-form-ipt-style",prefix:(0,o.jsx)(T,{style:{color:"#fff"}}),placeholder:i.default.get({id:"spg.pages.Login.PleaseEnterPassword",dm:"\u8BF7\u8F93\u5165\u5BC6\u7801"})})})]}),(0,o.jsx)(h.default.Item,{className:"submit-btn",children:(0,o.jsx)(f.default,{htmlType:"submit",type:"primary",loading:c,children:i.default.get({id:"spg.pages.Login.Login",dm:"\u767B\u5F55"})})})]})})]}),(0,o.jsx)(K,{open:a})]});};}}]);
