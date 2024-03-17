package com.mojh.cms.common.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode

object ProjectConfig : AbstractProjectConfig() {

    override val isolationMode = IsolationMode.InstancePerTest

}