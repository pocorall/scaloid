/*
 *
 *
 *
 * Less painful Android development with Scala
 *
 *
 * http://scaloid.org
 *
 *
 *
 *
 *
 *
 * Copyright 2013 Sung-Ho Lee and Scaloid team
 *
 * Sung-Ho Lee and Scaloid team licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.scaloid;

import android.provider.ContactsContract;

/**
 * ## Static fields on protected interfaces
 * <p/>
 * Android API has some protected interfaces which has static fields, and inherited it in public classes.
 * For example `android.provider.ContactsContract.Contacts` inherits a protected interface
 * `android.provider.ContactsContract.ContactsColumns`, which defines a static field `ContactsColumns.DISPLAY_NAME`.
 * In Java code, you can access it with `Contacts.DISPLAY_NAME`. However, Scala does not support accessing in this way
 * (please refer [this](https://issues.scala-lang.org/browse/SI-1806) and [this](http://www.scala-lang.org/faq/4)).
 * It is a bad news for Android-Scala programmer. So we provide a workaround implementation for this problem. Just
 * copy-and-paste `Workaround.java` and declare `import org.scaloid.Workarounds._`.
 * Then you can use the interfaces publicly which is originally defined as protected.
 */
public interface Workarounds {
    interface BaseColumns {
        String _ID = ContactsContract.RawContacts._ID;
        String _COUNT = ContactsContract.RawContacts._COUNT;
    }

    interface RawContactsColumns {
        String CONTACT_ID = ContactsContract.RawContacts.CONTACT_ID;
        String AGGREGATION_MODE = ContactsContract.RawContacts.AGGREGATION_MODE;
        String DELETED = ContactsContract.RawContacts.DELETED;
    }

    interface ContactsColumns {
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String PHOTO_ID = ContactsContract.Contacts.PHOTO_ID;
        String IN_VISIBLE_GROUP = ContactsContract.Contacts.IN_VISIBLE_GROUP;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;
    }

    interface ContactStatusColumns {
        String CONTACT_PRESENCE = ContactsContract.Contacts.CONTACT_PRESENCE;
        // Not available in API Level 10
//		String CONTACT_CHAT_CAPABILITY = ContactsContract.Contacts.CONTACT_CHAT_CAPABILITY;
        String CONTACT_STATUS = ContactsContract.Contacts.CONTACT_STATUS;
        String CONTACT_STATUS_TIMESTAMP = ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP;
        String CONTACT_STATUS_RES_PACKAGE = ContactsContract.Contacts.CONTACT_STATUS_RES_PACKAGE;
        String CONTACT_STATUS_LABEL = ContactsContract.Contacts.CONTACT_STATUS_LABEL;
        String CONTACT_STATUS_ICON = ContactsContract.Contacts.CONTACT_STATUS_ICON;
    }

    interface ContactOptionsColumns {
        String TIMES_CONTACTED = ContactsContract.RawContacts.TIMES_CONTACTED;
        String LAST_TIME_CONTACTED = ContactsContract.RawContacts.LAST_TIME_CONTACTED;
        String STARRED = ContactsContract.RawContacts.STARRED;
        String CUSTOM_RINGTONE = ContactsContract.RawContacts.CUSTOM_RINGTONE;
        String SEND_TO_VOICEMAIL = ContactsContract.RawContacts.SEND_TO_VOICEMAIL;
    }

    interface ContactNameColumns {
        // Not available in API Level 10
//		String DISPLAY_NAME_SOURCE = ContactsContract.RawContacts.DISPLAY_NAME_SOURCE;
//		String DISPLAY_NAME_PRIMARY = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY;
//		String DISPLAY_NAME_ALTERNATIVE = ContactsContract.RawContacts.DISPLAY_NAME_ALTERNATIVE;
//		String PHONETIC_NAME_STYLE = ContactsContract.RawContacts.PHONETIC_NAME_STYLE;
//		String PHONETIC_NAME = ContactsContract.RawContacts.PHONETIC_NAME;
//		String SORT_KEY_PRIMARY = ContactsContract.RawContacts.SORT_KEY_PRIMARY;
//		String SORT_KEY_ALTERNATIVE = ContactsContract.RawContacts.SORT_KEY_ALTERNATIVE;
    }

    interface BaseSyncColumns {
        String SYNC1 = ContactsContract.RawContacts.SYNC1;
        String SYNC2 = ContactsContract.RawContacts.SYNC2;
        String SYNC3 = ContactsContract.RawContacts.SYNC3;
        String SYNC4 = ContactsContract.RawContacts.SYNC4;
    }

    interface SyncColumns extends BaseSyncColumns {
        String ACCOUNT_NAME = ContactsContract.RawContacts.ACCOUNT_NAME;
        String ACCOUNT_TYPE = ContactsContract.RawContacts.ACCOUNT_TYPE;
        String SOURCE_ID = ContactsContract.RawContacts.SOURCE_ID;
        String VERSION = ContactsContract.RawContacts.VERSION;
        String DIRTY = ContactsContract.RawContacts.DIRTY;
    }

    interface DataColumns {
        String MIMETYPE = ContactsContract.Data.MIMETYPE;
        String RAW_CONTACT_ID = ContactsContract.Data.RAW_CONTACT_ID;
        String IS_PRIMARY = ContactsContract.Data.IS_PRIMARY;
        String IS_SUPER_PRIMARY = ContactsContract.Data.IS_SUPER_PRIMARY;
        String DATA_VERSION = ContactsContract.Data.DATA_VERSION;
        String DATA1 = ContactsContract.Data.DATA1;
        String DATA2 = ContactsContract.Data.DATA2;
        String DATA3 = ContactsContract.Data.DATA3;
        String DATA4 = ContactsContract.Data.DATA4;
        String DATA5 = ContactsContract.Data.DATA5;
        String DATA6 = ContactsContract.Data.DATA6;
        String DATA7 = ContactsContract.Data.DATA7;
        String DATA8 = ContactsContract.Data.DATA8;
        String DATA9 = ContactsContract.Data.DATA9;
        String DATA10 = ContactsContract.Data.DATA10;
        String DATA11 = ContactsContract.Data.DATA11;
        String DATA12 = ContactsContract.Data.DATA12;
        String DATA13 = ContactsContract.Data.DATA13;
        String DATA14 = ContactsContract.Data.DATA14;
        String DATA15 = ContactsContract.Data.DATA15;
    }

}
