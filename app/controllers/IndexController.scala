/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import config.{FrontendAppConfig, ManagerConfig}
import viewmodels.RadioOption
import views.html.selector

class IndexController @Inject()(val appConfig: FrontendAppConfig,
                                val messagesApi: MessagesApi,
                                val profilesConfig: ManagerConfig) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action { implicit request =>
    val testEnvironments = profilesConfig.getTestEnvironments
    val profiles = profilesConfig.getProfiles
    val radioOptions = testEnvironments.map(
      testEnvironment => RadioOption(testEnvironment.name, testEnvironment.name)
    )
    Ok(selector(appConfig ,testEnvironments, profiles))
  }

}
