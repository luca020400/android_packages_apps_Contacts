/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.contacts.common.model;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.contacts.common.model.account.AccountWithDataSet;
import com.android.contacts.common.model.account.GoogleAccountType;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * Tests for {@link AccountTypeManager#getDefaultGoogleAccount()}
 */
@SmallTest
public class AccountTypeManagerDefaultAccountTests extends InstrumentationTestCase {

    private static final Account[] ACCOUNTS = new Account[2];
    static {
        ACCOUNTS[0] = new Account("name1", GoogleAccountType.ACCOUNT_TYPE);
        ACCOUNTS[1] = new Account("name2", GoogleAccountType.ACCOUNT_TYPE);
    }

    @Mock private AccountManager mAccountManager;
    @Mock private SharedPreferences mPrefs;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getPath());
        MockitoAnnotations.initMocks(this);
    }

    public void testGetDefaultAccount_NoAccounts() {
        assertNull(getDefaultGoogleAccountName());
    }

    public void testGetDefaultAccount_NoAccounts_DefaultPreferenceSet() {
        when(mPrefs.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(
                getDefaultAccountPreference("name1", GoogleAccountType.ACCOUNT_TYPE));
        assertNull(getDefaultGoogleAccountName());
    }

    public void testGetDefaultAccount_NoDefaultAccountPreferenceSet() {
        when(mAccountManager.getAccountsByType(Mockito.anyString())).thenReturn(ACCOUNTS);
        assertEquals("name1", getDefaultGoogleAccountName());
    }

    public void testGetDefaultAccount_DefaultAccountPreferenceSet() {
        when(mAccountManager.getAccountsByType(Mockito.anyString())).thenReturn(ACCOUNTS);
        when(mPrefs.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(
                getDefaultAccountPreference("name2", GoogleAccountType.ACCOUNT_TYPE));
        assertEquals("name2", getDefaultGoogleAccountName());
    }

    public void testGetDefaultAccount_DefaultAccountPreferenceSet_NonGoogleAccountType() {
        when(mAccountManager.getAccountsByType(Mockito.anyString())).thenReturn(ACCOUNTS);
        when(mPrefs.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(
                getDefaultAccountPreference("name3", "type3"));
        assertEquals("name1", getDefaultGoogleAccountName());
    }

    public void testGetDefaultAccount_DefaultAccountPreferenceSet_UnknownName() {
        when(mAccountManager.getAccountsByType(Mockito.anyString())).thenReturn(ACCOUNTS);
        when(mPrefs.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(
                getDefaultAccountPreference("name4",GoogleAccountType.ACCOUNT_TYPE));
        assertEquals("name1", getDefaultGoogleAccountName());
    }

    private final String getDefaultGoogleAccountName() {
        // We don't need the real preference key value since it's mocked
        final Account account = AccountTypeManager.getDefaultGoogleAccount(
                mAccountManager, mPrefs, "contact_editor_default_account_key");
        return account == null ? null : account.name;
    }

    private static final String getDefaultAccountPreference(String name, String type) {
        return new AccountWithDataSet(name, type, /* dataSet */ null).stringify();
    }
}