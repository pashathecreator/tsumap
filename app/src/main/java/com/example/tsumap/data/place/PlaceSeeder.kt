package com.example.tsumap.data.place

object PlaceSeeder {
    fun getInitialPlaces() = listOf(
        PlaceEntity("main",      "Главный корпус",                     "🏛️", "place_main",      53, 67,  "BUILDING"),
        PlaceEntity("ck",        "Центр культуры",                     "🎭", "place_ck",        58, 56,  "BUILDING"),
        PlaceEntity("sport",     "ФФК",                                "🏋️", "place_sport",     73, 51,  "BUILDING"),
        PlaceEntity("lyceum",    "Лицей ТГУ",                          "🏫", "place_lyceum",    59, 33,  "BUILDING"),
        PlaceEntity("ftf",       "ФТФ",                                "🏛️", "place_ftf",       72, 32,  "BUILDING"),
        PlaceEntity("second",    "Второй корпус",                      "🏛️", "place_second",    66, 43,  "BUILDING"),
        PlaceEntity("law",       "Юридический институт",               "⚖️", "place_law",       46, 20,  "BUILDING"),
        PlaceEntity("library",   "Научная библиотека",                 "📚", "place_library",   76, 87,  "BUILDING"),
        PlaceEntity("history",   "ФИП",                                "🏛️", "place_history",   84, 88,  "BUILDING"),
        PlaceEntity("economics", "ИЭМ",                                "🏛️", "place_economics", 13, 94,  "BUILDING"),
        PlaceEntity("physics",   "СФТИ",                               "🔬", "place_physics",   4,  99,  "BUILDING"),
        PlaceEntity("draw",      "Кафедра изобразительного искусства", "🎨", "place_draw",      19, 118, "BUILDING"),

        PlaceEntity("pancakes",  "Сибирские блины", "🥞", "place_pancakes",  53, 56, "CAFE"),
        PlaceEntity("cafe_main", "Столовая",        "🍽️", "place_cafe_main", 50, 56, "CAFE"),
        PlaceEntity("starbooks", "StarBooks",        "☕", "place_starbooks", 52, 51, "CAFE"),
        PlaceEntity("coffe1",    "Белка кофе",       "☕", "place_coffe1",    25, 86, "CAFE"),
        PlaceEntity("yarche",    "Ярче",             "🥪", "place_yarche",    22, 119,"CAFE"),
        PlaceEntity("coffe2",    "Пеки Лола",        "☕", "place_coffe2",    20, 129,"CAFE"),
        PlaceEntity("our",       "Гастроном",        "🥪", "place_our",       52, 119,"CAFE"),
        PlaceEntity("apricot",   "Абрикос","🥪", "place_apricot",   21, 6,   "CAFE"),


        PlaceEntity("monument1", "Павшим сотрудникам и студентам", "🗽", "place_monument1", 66, 76, "MONUMENT"),
        PlaceEntity("monument2", "Г.Н. Потанину",                  "🗽", "place_monument2", 64, 83, "MONUMENT"),
    )
}