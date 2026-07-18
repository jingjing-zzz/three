/**
 * 针对 https://github.com/xaboy/form-create-designer 封装的工具类
 */
import { isRef } from 'vue'
import formCreate from '@form-create/element-ui'

/** 编码表单 Conf */
export const encodeConf = (designerRef: object) => {
  // @ts-ignore
  // 关联案例：https://gitee.com/mitedtsmcode/mitedtsm-ui-admin-vue3/pulls/834/
  return formCreate.toJson(designerRef.value.getOption())
}

/** 解码表单 Conf */
export const decodeConf = (conf: string) => {
  return formCreate.parseJson(conf)
}

/** 编码表单 Fields */
export const encodeFields = (designerRef: object) => {
  // @ts-ignore
  const rule = designerRef.value.getRule()
  const fields: string[] = []
  rule.forEach((item: any) => {
    fields.push(formCreate.toJson(item))
  })
  return fields
}

/** 解码表单 Fields */
export const decodeFields = (fields: string[]) => {
  const rule: object[] = []
  fields.forEach((item) => {
    const parsed = formCreate.parseJson(item) as any
    
    if (parsed.$required || parsed.required) {
      parsed.required = true
      if (parsed.$required) {
        delete parsed.$required
      }
      
      if (!parsed.validate) {
        parsed.validate = []
      }
      
      const hasRequiredRule = parsed.validate.some((v: any) => v.required)
      if (!hasRequiredRule) {
        parsed.validate.push({ required: true, message: `${parsed.title}不能为空`, trigger: 'change' })
      } else {
        parsed.validate.forEach((v: any) => {
          if (v.required && v.trigger === 'blur') {
            v.trigger = 'change'
          }
        })
      }
      
      if (!parsed.effect) {
        parsed.effect = {}
      }
      parsed.effect.required = true
    }
    
    rule.push(parsed)
  })
  return rule
}

/** 设置表单的 Conf 和 Fields，适用 FcDesigner 场景 */
export const setConfAndFields = (designerRef: object, conf: string, fields: string[]) => {
  // @ts-ignore
  designerRef.value.setOption(decodeConf(conf))
  // @ts-ignore
  designerRef.value.setRule(decodeFields(fields))
}

/** 设置表单的 Conf 和 Fields，适用 form-create 场景 */
export const setConfAndFields2 = (
  detailPreview: object,
  conf: string,
  fields: string[],
  value?: object
) => {
  if (isRef(detailPreview)) {
    // @ts-ignore
    detailPreview = detailPreview.value
  }

  const rule = decodeFields(fields)
  const option = decodeConf(conf)

  // @ts-ignore
  detailPreview.option = option
  // @ts-ignore
  detailPreview.rule = rule

  if (value) {
    // @ts-ignore
    detailPreview.value = value
  }

  return { rule, option }
}
