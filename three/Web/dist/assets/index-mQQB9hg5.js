import{_ as D}from"./Dialog-DmYCesWz.js";import{_ as J}from"./ContentWrap-DLw_C67s.js";import{b as j,d as T,aK as V,T as I,_ as k}from"./index-q-2PArRJ.js";import{y as F,r as d,l as z,ax as N,u as A,aj as B,ar as M,z as b,A as _,Q as n,H as l,D as h,M as f,O as L,I as P,L as R,P as E}from"./form-create-CirSv6Vh.js";import{u as H}from"./useFormCreateDesigner-ldtXp2om.js";import{H as v}from"./index-L8xqCnJY.js";import{j as W}from"./java-Cb798QjB.js";import{j as $}from"./json-D1vBrYzr.js";import{h as K}from"./form-designer-CstCQlzk.js";import"./index-BCN8BzfC.js";import"./dict.type-Cr46lV8p.js";const Q={class:"h-[calc(100vh-var(--top-tool-height)-var(--tags-view-height)-var(--app-content-padding)-var(--app-content-padding)-2px)]"},U={key:0,ref:"editor"},q={class:"mt-10px"},G={class:"hljs"},X=k(F({name:"InfraBuild",__name:"index",setup(C,{expose:o}){o();const{t:c}=j(),e=T(),y=d({switchType:[],autoActive:!0,useTemplate:!1,formOptions:{form:{labelWidth:"100px"}},fieldReadonly:!1,hiddenDragMenu:!1,hiddenDragBtn:!1,hiddenMenu:[],hiddenItem:[],hiddenItemConfig:{},disabledItemConfig:{},showSaveBtn:!1,showConfig:!0,showBaseForm:!0,showControl:!0,showPropsForm:!0,showEventForm:!0,showValidateForm:!0,showFormConfig:!0,showInputData:!0,showDevice:!0,appendConfigData:[]}),t=d(),i=d(!1),g=d(""),s=d(-1),r=d("");H(t);const p=a=>{i.value=!0,g.value=a},u=()=>{const a=t.value.getRule(),m=t.value.getOption();return`<template>
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
      rule.value = formCreate.parseJson('${N.toJson(a).replaceAll("\\","\\\\")}')
      option.value = formCreate.parseJson('${JSON.stringify(m)}')
    }
    const onSubmit = (formData) => {
      //todo \u63D0\u4EA4\u8868\u5355
    }
    init()
  <\/script>`};z(async()=>{v.registerLanguage("xml",W),v.registerLanguage("json",$)});const w={t:c,message:e,designerConfig:y,designer:t,dialogVisible:i,dialogTitle:g,formType:s,formData:r,openModel:p,showJson:()=>{p("\u751F\u6210 JSON"),s.value=0,r.value=t.value.getRule()},showOption:()=>{p("\u751F\u6210 Options"),s.value=1,r.value=t.value.getOption()},showTemplate:()=>{p("\u751F\u6210\u7EC4\u4EF6"),s.value=2,r.value=u()},makeTemplate:u,copy:async a=>{const m=JSON.stringify(a,null,2),{copy:O,copied:x,isSupported:S}=V({legacy:!0,source:m});S?(await O(),A(x)&&e.success(c("common.copySuccess"))):e.error(c("common.copyError"))},highlightedCode:a=>{let m="json";return s.value===2&&(m="xml"),I(a)||(a=JSON.stringify(a,null,2)),v.highlight(a,{language:m,ignoreIllegals:!0}).value||"&nbsp;"}};return Object.defineProperty(w,"__isScriptSetup",{enumerable:!1,value:!0}),w}}),[["render",function(C,o,c,e,y,t){const i=K,g=B("fc-designer"),s=J,r=D,p=M("dompurify-html");return b(),_(E,null,[n(s,{"body-style":{padding:"0px"},class:"!mb-0"},{default:l(()=>[h("div",Q,[n(g,{class:"my-designer",ref:"designer",config:e.designerConfig},{handle:l(()=>[n(i,{size:"small",type:"primary",plain:"",onClick:e.showJson},{default:l(()=>o[2]||(o[2]=[f("\u751F\u6210JSON")])),_:1}),n(i,{size:"small",type:"success",plain:"",onClick:e.showOption},{default:l(()=>o[3]||(o[3]=[f("\u751F\u6210Options")])),_:1}),n(i,{size:"small",type:"danger",plain:"",onClick:e.showTemplate},{default:l(()=>o[4]||(o[4]=[f("\u751F\u6210\u7EC4\u4EF6")])),_:1})]),_:1},8,["config"])])]),_:1}),n(r,{modelValue:e.dialogVisible,"onUpdate:modelValue":o[1]||(o[1]=u=>e.dialogVisible=u),title:e.dialogTitle,"max-height":"600"},{default:l(()=>[e.dialogVisible?(b(),_("div",U,[n(i,{style:{float:"right"},onClick:o[0]||(o[0]=u=>e.copy(e.formData))},{default:l(()=>[f(L(e.t("common.copy")),1)]),_:1}),h("pre",q,[P(h("code",G,null,512),[[p,e.highlightedCode(e.formData)]])])],512)):R("",!0)]),_:1},8,["modelValue","title"])],64)}],["__file","/home/zyf/project/mixun/three/Web/src/views/infra/build/index.vue"]]);export{X as default};
