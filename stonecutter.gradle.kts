plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.8.9" /* [SC] DO NOT EDIT */

stonecutter {
    tasks {
        order("publishModrinth")
    }
}
