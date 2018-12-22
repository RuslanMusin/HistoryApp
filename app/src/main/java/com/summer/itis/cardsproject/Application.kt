package com.summer.itis.cardsproject

/*
 *  Copyright 2017 Rozdoum
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
 *
 */


import android.content.Context
import android.support.multidex.MultiDexApplication
import com.summer.itis.cardsproject.utils.ApplicationHelper

class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        ApplicationHelper.initUserState(this)
    }



    companion object {

        val TAG = Application::class.java.simpleName
    }
}