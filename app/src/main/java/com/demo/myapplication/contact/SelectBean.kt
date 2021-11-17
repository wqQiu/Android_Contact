package com.fooww.module.sdk.ui.contact

import java.io.Serializable

/**
 * created by wangwq on 2021/11/5
 */
data class SelectBean(
  var id: String? = "",
  var name: String? = "",
  var phone: String? = ""
) : Serializable {
  override fun toString(): String {
    return "SelectBean(id=$id, name=$name, phone=$phone)"
  }
}