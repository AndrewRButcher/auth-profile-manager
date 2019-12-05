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

package config

import com.typesafe.config.ConfigException.Missing
import play.api.{Configuration, Environment, Logger}
import javax.inject.{Inject, Singleton}
import models.{Profile, TestEnvironment}
import uk.gov.hmrc.play.config.RunMode

import scala.collection.JavaConversions._

  @Singleton
class ManagerConfig @Inject()(override val runModeConfiguration: Configuration,
                              environment: Environment, configuration: Configuration) extends RunMode{
    override protected def mode = environment.mode

    val config = configuration.underlying

    def getURL(testEnvironmentName:String) = {
      try{
        config.getString(s"environments.$testEnvironmentName.url")
      } catch {
        case _: Missing => Logger.warn(s"[ManagerConfig][getURL] Missing URL config for $testEnvironmentName test environment.")
          ""
      }
    }

    def getID(testEnvironmentName:String) = {
      try{
        config.getString(s"environments.$testEnvironmentName.id")
      } catch {
        case _: Missing => Logger.warn(s"[ManagerConfig][getURL] Missing ID config for $testEnvironmentName test environment.")
          ""
      }
    }

    def getProfileDetail(profileName: String, detail:String):Option[String] = {
      try {
        Some(config.getString(s"profiles.$profileName.$detail"))
      } catch {
        case _: Missing => Logger.warn(s"[ManagerConfig][getProfileDetail] Missing $detail config for $profileName profile.")
          None
      }
    }

    def getProfileCredentialStrength(profileName: String):String = {
      try {
        config.getString(s"profiles.$profileName.Credential Strength")
      } catch {
        case _: Missing => Logger.warn(s"[ManagerConfig][getProfileCredentialStrength] Missing Credential Strength config for $profileName profile, defaulting to weak.")
          "weak"
      }
    }

    def getProfileConfidenceLevel(profileName: String):Int = {
      try {
        config.getInt(s"profiles.$profileName.Confidence Level")
      } catch {
        case _: Missing => Logger.warn(s"[ManagerConfig][getProfileConfidenceLevel] Missing Confidence Level config for $profileName profile, defaulting to 50.")
          50
      }
    }

    def getProfileAffinityGroup(profileName: String):String = {
      try {
        config.getString(s"profiles.$profileName.Affinity Group")
      } catch {
        case _: Missing => Logger.warn(s"[ManagerConfig][getProfileConfidenceLevel] Missing Affinity Group config for $profileName profile, defaulting to Individual.")
          "Individual"
      }
    }

    def getEnvironmentDetails(testEnvironmentName: String):Option[TestEnvironment] ={
      Some(TestEnvironment(testEnvironmentName, getURL(testEnvironmentName)))
    }

    def getTestEnvironments():Seq[TestEnvironment] = {
      config.getConfig("environments").root().keySet().flatMap(
        implicit testEnvironmentName =>
          getEnvironmentDetails(testEnvironmentName).map(
            testEnvironmentDetails => Some(testEnvironmentDetails)
          )
      ).flatten.toSeq
    }

    def getProfileDetails(name: String): Option[Profile] = {
      Some(Profile(
        name,
        getProfileDetail(name ,"CredID"),
        getProfileDetail(name ,"RedirectURL"),
        getProfileCredentialStrength(name),
        getProfileConfidenceLevel(name),
        getProfileAffinityGroup(name),
        getProfileDetail(name ,"NINO")))
    }

    def getProfiles():Seq[Profile] = {
      config.getConfig("profiles").root().keySet().flatMap(
        implicit profileName =>
          getProfileDetails(profileName).map(
            profileDetails => Some(profileDetails)
          )
      ).flatten.toSeq

    }
  }