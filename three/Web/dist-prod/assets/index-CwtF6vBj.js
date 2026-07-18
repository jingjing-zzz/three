import{_ as V}from"./Dialog.vue_vue_type_style_index_0_lang-Cac0WR2u.js";import{_ as L}from"./ContentWrap.vue_vue_type_script_setup_true_lang-B679scv2.js";import{b as M,d as R,aK as T,T as E}from"./index-OzpuZxOg.js";import{u as H}from"./useFormCreateDesigner-Cmw5kZuh.js";import{H as f}from"./index-CYIIxFJ1.js";import{j as P}from"./java-Cb798QjB.js";import{j as $}from"./json-D1vBrYzr.js";import{y as U,r as l,l as W,aj as K,ar as Q,z as w,A as C,Q as n,H as s,D as g,u as o,M as m,O as q,I as G,L as X,j as Y,P as Z,ax as ee}from"./form-create-B8yzM4C0.js";import{f as ae}from"./form-designer-CKNVE2nQ.js";import"./index-BCN8BzfC.js";import"./dict.type-BdtSrGae.js";const oe={class:"h-[calc(100vh-var(--top-tool-height)-var(--tags-view-height)-var(--app-content-padding)-var(--app-content-padding)-2px)]"},ne={key:0,ref:"editor"},se={class:"mt-10px"},te={class:"hljs"},le=U({name:"InfraBuild",__name:"index",setup(re){const{t:c}=M(),h=R(),_=l({switchType:[],autoActive:!0,useTemplate:!1,formOptions:{form:{labelWidth:"100px"}},fieldReadonly:!1,hiddenDragMenu:!1,hiddenDragBtn:!1,hiddenMenu:[],hiddenItem:[],hiddenItemConfig:{},disabledItemConfig:{},showSaveBtn:!1,showConfig:!0,showBaseForm:!0,showControl:!0,showPropsForm:!0,showEventForm:!0,showValidateForm:!0,showFormConfig:!0,showInputData:!0,showDevice:!0,appendConfigData:[]}),t=l(),r=l(!1),v=l(""),p=l(-1),i=l("");H(t);const d=a=>{r.value=!0,v.value=a},O=()=>{d("\u751F\u6210 JSON"),p.value=0,i.value=t.value.getRule()},x=()=>{d("\u751F\u6210 Options"),p.value=1,i.value=t.value.getOption()},S=()=>{d("\u751F\u6210\u7EC4\u4EF6"),p.value=2,i.value=b()},b=()=>{const a=t.value.getRule(),e=t.value.getOption();return`<template>
    <form-create
      v-model:api="fApi"
      :rule="rule"
      :option="option"
      @submit="onSubmit"
    ></form-create>
  </template>
  <script setup lang=ts>
    const faps = ref(null)
    const rule = ref('')
    const option = ref('')
    const init = () => {
      rule.value = formCreate.parseJson('${ee.toJson(a).replaceAll("\\","\\\\")}')
      option.value = formCreate.parseJson('${JSON.stringify(e)}')
    }
    const onSubmit = (formData) => {
      //todo \u63D0\u4EA4\u8868\u5355
    }
    init()
  <\/script>`},J=a=>{let e="json";return p.value===2&&(e="xml"),E(a)||(a=JSON.stringify(a,null,2)),f.highlight(a,{language:e,ignoreIllegals:!0}).value||"&nbsp;"};return W(async()=>{f.registerLanguage("xml",P),f.registerLanguage("json",$)}),(a,e)=>{const u=ae,j=K("fc-designer"),D=L,I=V,k=Q("dompurify-html");return w(),C(Z,null,[n(D,{"body-style":{padding:"0px"},class:"!mb-0"},{default:s(()=>[g("div",oe,[n(j,{class:"my-designer",ref_key:"designer",ref:t,config:o(_)},{handle:s(()=>[n(u,{size:"small",type:"primary",plain:"",onClick:O},{default:s(()=>e[2]||(e[2]=[m("\u751F\u6210JSON")])),_:1}),n(u,{size:"small",type:"success",plain:"",onClick:x},{default:s(()=>e[3]||(e[3]=[m("\u751F\u6210Options")])),_:1}),n(u,{size:"small",type:"danger",plain:"",onClick:S},{default:s(()=>e[4]||(e[4]=[m("\u751F\u6210\u7EC4\u4EF6")])),_:1})]),_:1},8,["config"])])]),_:1}),n(I,{modelValue:o(r),"onUpdate:modelValue":e[1]||(e[1]=y=>Y(r)?r.value=y:null),title:o(v),"max-height":"600"},{default:s(()=>[o(r)?(w(),C("div",ne,[n(u,{style:{float:"right"},onClick:e[0]||(e[0]=y=>(async z=>{const F=JSON.stringify(z,null,2),{copy:N,copied:A,isSupported:B}=T({legacy:!0,source:F});B?(await N(),o(A)&&h.success(c("common.copySuccess"))):h.error(c("common.copyError"))})(o(i)))},{default:s(()=>[m(q(o(c)("common.copy")),1)]),_:1}),g("pre",se,[G(g("code",te,null,512),[[k,J(o(i))]])])],512)):X("",!0)]),_:1},8,["modelValue","title"])],64)}}});export{le as default};
