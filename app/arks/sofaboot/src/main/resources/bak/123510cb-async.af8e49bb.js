(("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]=("undefined"!=typeof globalThis?globalThis:self)["makoChunk_open-semantic-graph"]||[]).push([["123510cb"],{"5e00c259":function(e,l,a){"use strict";a.d(l,"__esModule",{value:!0}),a.d(l,"Title",{enumerable:!0,get:function(){return s;}});var i=a("32b7a2cf"),n=a("5b5ed4a9");a("4d0e37ae");var t=a("fc1f4356");let d=n.styled.div`
  .header {
    color: var(--dark-shade-85);
    font-weight: var(--font-medium);
    font-size: ${({$level:e})=>"page"===e?"var(--h1-font-size)":"var(--h3-font-size)"};
    line-height: ${({$level:e})=>"page"===e?"28px":"24px"};
  }
`,s=({className:e,style:l,level:a="page",title:s,titleExtra:o,showBack:r=!1,goBackCb:c,children:f})=>{let u=(0,n.useNavigate)();return(0,i.jsxs)(d,{$level:a,className:e,style:l,children:[(0,i.jsxs)("div",{className:"flex-row mb16",children:[(0,i.jsxs)("div",{className:"header",children:[r&&(0,i.jsx)(n.Icon,{className:"icon mr8 pointer",icon:"ant-design:arrow-left-outlined",onClick:()=>(0,t.isFunction)(c)?c():u(-1)}),s]}),(0,i.jsx)("div",{children:o})]}),(0,i.jsx)("div",{children:f})]});};},"7e832075":function(e,l,a){"use strict";a.d(l,"__esModule",{value:!0}),a.d(l,"default",{enumerable:!0,get:function(){return r;}});var i=a("777fffbe"),n=a("32b7a2cf"),t=a("5e00c259"),d=i._(a("f02131d0")),s=a("5b5ed4a9");a("4d0e37ae");let o=s.styled.div`
  padding: 24px 40px 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
`;var r=()=>(0,n.jsxs)(o,{children:[(0,n.jsx)(t.Title,{level:"page",title:d.default.get({id:"spg.KnowledgeModeling.KnowledgeExploration.KnowledgeExploration",dm:"\u77E5\u8BC6\u63A2\u67E5"})}),(0,n.jsx)(s.Outlet,{})]});}}]);
