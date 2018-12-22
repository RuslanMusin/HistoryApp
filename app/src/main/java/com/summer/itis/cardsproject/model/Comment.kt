/*
 * Copyright 2017 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.summer.itis.summerproject.model


import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

import java.util.Calendar

@IgnoreExtraProperties
class Comment {

    var id: String? = null
    var text: String? = null
    var authorId: String? = null
    var createdDate: Long = 0

    @Exclude
    @Transient
    var authorName: String? = null

    @Exclude
    @Transient
    var authorPhotoUrl: String? = null


    constructor() {}

    constructor(text: String) {
        this.text = text
        this.createdDate = Calendar.getInstance().timeInMillis
    }
}
