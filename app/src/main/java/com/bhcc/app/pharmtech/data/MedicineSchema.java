package com.bhcc.app.pharmtech.data;

public class MedicineSchema {
    public static final class MedicineTable {
        public static final String NAME = "TopDrugs";

        public static final class Cols {
            public static final String GENERIC_NAME = "GenericName";
            public static final String BRAND_NAME = "BrandName";
            public static final String PURPOSE = "Purpose";
            public static final String DEASCH = "DEASch";
            public static final String SPECIAL = "Special";
            public static final String CATEGORY = "Category";
            public static final String STUDY_TOPIC = "StudyTopic";
        }

        public static final int NUM_COLS = 7;
    }
}
