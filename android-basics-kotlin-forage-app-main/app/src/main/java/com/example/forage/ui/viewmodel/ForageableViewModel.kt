/*
 * Copyright (C) 2021 The Android Open Source Project.
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
package com.example.forage.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import com.example.forage.data.ForageableDao
import com.example.forage.model.Forageable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Shared [ViewModel] to provide data to the [ForageableListFragment], [ForageableDetailFragment],
 * and [AddForageableFragment] and allow for interaction the the [ForageableDao]
 */

// TODO: pass a ForageableDao value as a parameter to the view model constructor
class ForageableViewModel(
    private val forageableDao: ForageableDao
): ViewModel() {


    val items: LiveData<List<Forageable>> = forageableDao.retrieveAllItems().asLiveData()
    // TODO: create a property to set to a list of all forageables from the DAO

    fun getItem(id: Long): LiveData<Forageable> {
        return forageableDao.retrieveItem(id.toInt()).asLiveData()
    }
    // TODO : create method that takes id: Long as a parameter and retrieve a Forageable from the
    //  database by id via the DAO.

    fun addForageable(
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String
    ) {
        val forageable = Forageable(
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )

        if (isValidEntry(forageable.name, forageable.address))
            viewModelScope.launch {
                forageableDao.insert(forageable)
            }
    // TODO: launch a coroutine and call the DAO method to add a Forageable to the database within it

    }

    fun updateForageable(
        id: Long,
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String
    ) {
        val forageable = Forageable(
            id = id,
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )
        viewModelScope.launch(Dispatchers.IO) {
            forageableDao.update(forageable)
        }
    }

    fun deleteForageable(forageable: Forageable) {
        viewModelScope.launch(Dispatchers.IO) {
            forageableDao.delete(forageable)
        }
    }

    fun isValidEntry(name: String, address: String): Boolean {
        return name.isNotBlank() && address.isNotBlank()
    }
}
/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class ForageableViewModelFactory(private val itemDao: ForageableDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForageableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForageableViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// TODO: create a view model factory that takes a ForageableDao as a property and
//  creates a ForageableViewModel
