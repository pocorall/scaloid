package net.pocorall.android;

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
 * copy-and-paste `Workaround.java` and declare `import net.pocorall.android.Workarounds._`.
 * Then you can use the interfaces publicly which is originally defined as protected.
 */
public class Workarounds {
	public static interface BaseColumns {
		static public String _ID = ContactsContract.RawContacts._ID;
		static public String _COUNT = ContactsContract.RawContacts._COUNT;
	}

	public static interface RawContactsColumns {
		static public String CONTACT_ID = ContactsContract.RawContacts.CONTACT_ID;
		static public String AGGREGATION_MODE = ContactsContract.RawContacts.AGGREGATION_MODE;
		static public String DELETED = ContactsContract.RawContacts.DELETED;
	}

	public static interface ContactsColumns {
		static public String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		static public String PHOTO_ID = ContactsContract.Contacts.PHOTO_ID;
		static public String IN_VISIBLE_GROUP = ContactsContract.Contacts.IN_VISIBLE_GROUP;
		static public String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		static public String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;
	}

	public static interface ContactStatusColumns {
		static public String CONTACT_PRESENCE = ContactsContract.Contacts.CONTACT_PRESENCE;
		static public String CONTACT_CHAT_CAPABILITY = ContactsContract.Contacts.CONTACT_CHAT_CAPABILITY;
		static public String CONTACT_STATUS = ContactsContract.Contacts.CONTACT_STATUS;
		static public String CONTACT_STATUS_TIMESTAMP = ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP;
		static public String CONTACT_STATUS_RES_PACKAGE = ContactsContract.Contacts.CONTACT_STATUS_RES_PACKAGE;
		static public String CONTACT_STATUS_LABEL = ContactsContract.Contacts.CONTACT_STATUS_LABEL;
		static public String CONTACT_STATUS_ICON = ContactsContract.Contacts.CONTACT_STATUS_ICON;
	}

	public static interface ContactOptionsColumns {
		static public String TIMES_CONTACTED = ContactsContract.RawContacts.TIMES_CONTACTED;
		static public String LAST_TIME_CONTACTED = ContactsContract.RawContacts.LAST_TIME_CONTACTED;
		static public String STARRED = ContactsContract.RawContacts.STARRED;
		static public String CUSTOM_RINGTONE = ContactsContract.RawContacts.CUSTOM_RINGTONE;
		static public String SEND_TO_VOICEMAIL = ContactsContract.RawContacts.SEND_TO_VOICEMAIL;
	}

	public static interface ContactNameColumns {
		static public String DISPLAY_NAME_SOURCE = ContactsContract.RawContacts.DISPLAY_NAME_SOURCE;
		static public String DISPLAY_NAME_PRIMARY = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY;
		static public String DISPLAY_NAME_ALTERNATIVE = ContactsContract.RawContacts.DISPLAY_NAME_ALTERNATIVE;
		static public String PHONETIC_NAME_STYLE = ContactsContract.RawContacts.PHONETIC_NAME_STYLE;
		static public String PHONETIC_NAME = ContactsContract.RawContacts.PHONETIC_NAME;
		static public String SORT_KEY_PRIMARY = ContactsContract.RawContacts.SORT_KEY_PRIMARY;
		static public String SORT_KEY_ALTERNATIVE = ContactsContract.RawContacts.SORT_KEY_ALTERNATIVE;
	}

	public static interface BaseSyncColumns {
		static public String SYNC1 = ContactsContract.RawContacts.SYNC1;
		static public String SYNC2 = ContactsContract.RawContacts.SYNC2;
		static public String SYNC3 = ContactsContract.RawContacts.SYNC3;
		static public String SYNC4 = ContactsContract.RawContacts.SYNC4;
	}

	public static interface SyncColumns extends BaseSyncColumns {
		static public String ACCOUNT_NAME = ContactsContract.RawContacts.ACCOUNT_NAME;
		static public String ACCOUNT_TYPE = ContactsContract.RawContacts.ACCOUNT_TYPE;
		static public String SOURCE_ID = ContactsContract.RawContacts.SOURCE_ID;
		static public String VERSION = ContactsContract.RawContacts.VERSION;
		static public String DIRTY = ContactsContract.RawContacts.DIRTY;
	}

	public static interface DataColumns {
		static public String MIMETYPE = ContactsContract.Data.MIMETYPE;
		static public String RAW_CONTACT_ID = ContactsContract.Data.RAW_CONTACT_ID;
		static public String IS_PRIMARY = ContactsContract.Data.IS_PRIMARY;
		static public String IS_SUPER_PRIMARY = ContactsContract.Data.IS_SUPER_PRIMARY;
		static public String DATA_VERSION = ContactsContract.Data.DATA_VERSION;
		static public String DATA1 = ContactsContract.Data.DATA1;
		static public String DATA2 = ContactsContract.Data.DATA2;
		static public String DATA3 = ContactsContract.Data.DATA3;
		static public String DATA4 = ContactsContract.Data.DATA4;
		static public String DATA5 = ContactsContract.Data.DATA5;
		static public String DATA6 = ContactsContract.Data.DATA6;
		static public String DATA7 = ContactsContract.Data.DATA7;
		static public String DATA8 = ContactsContract.Data.DATA8;
		static public String DATA9 = ContactsContract.Data.DATA9;
		static public String DATA10 = ContactsContract.Data.DATA10;
		static public String DATA11 = ContactsContract.Data.DATA11;
		static public String DATA12 = ContactsContract.Data.DATA12;
		static public String DATA13 = ContactsContract.Data.DATA13;
		static public String DATA14 = ContactsContract.Data.DATA14;
		static public String DATA15 = ContactsContract.Data.DATA15;
	}

}
