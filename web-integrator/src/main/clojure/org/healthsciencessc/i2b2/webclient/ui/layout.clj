(ns org.healthsciencessc.i2b2.webclient.ui.layout
  (:require [hiccup.page :as page]
            [hiccup.core :as hcup]
            [org.healthsciencessc.i2b2.webclient.auth :as auth])
  (:use     [pliant.process :only [defprocess deflayer continue]]))


(defprocess scripts
  "Generates a list of URLs that are injected into the head section of the layout as script files(javascript)."
  [request options]
  ["/js-ext/yui/build/yahoo/yahoo.js"
   "/js-ext/yui/build/event/event.js"
   "/js-ext/yui/build/dom/dom.js"
   "/js-ext/yui/build/yuiloader/yuiloader.js"
   "/js-ext/yui/build/dragdrop/dragdrop.js"
   "/js-ext/yui/build/element/element.js"
   "/js-ext/yui/build/container/container_core.js"
   "/js-ext/yui/build/container/container.js"
   "/js-ext/yui/build/resize/resize.js"
   "/js-ext/yui/build/utilities/utilities.js"
   "/js-ext/yui/build/menu/menu.js"
   "/js-ext/yui/build/calendar/calendar.js"
   "/js-ext/yui/build/treeview/treeview.js"
   "/js-ext/yui/build/tabview/tabview.js"
   "/js-ext/yui/build/animation/animation.js"
   "/js-ext/yui/build/datasource/datasource.js"
   "/js-ext/yui/build/yahoo-dom-event/yahoo-dom-event.js"
   "/js-ext/yui/build/json/json-min.js"
   "/js-ext/yui/build/datatable/datatable.js"
   "/js-ext/yui/build/button/button.js"
   "/js-ext/yui/build/paginator/paginator-min.js"
   "/js-ext/yui/build/slider/slider-min.js"])

(defprocess stylesheets
  "Generates a list of URLs that are injected into the head section of the layout as stylesheets."
  [request options]
  ["/js-ext/yui/build/assets/skins/sam/skin.css"
   "/js-i2b2/ui.styles/ui.styles.css"
   "/js-ext/yui/build/fonts/fonts-min.css"
   "/js-ext/yui/build/tabview/assets/skins/sam/tabview.css"
   "/js-ext/yui/build/menu/assets/skins/sam/menu.css"
   "/js-ext/yui/build/button/assets/skins/sam/button.css"
   "/js-ext/yui/build/container/assets/skins/sam/container.css"
   "/js-ext/yui/build/container/assets/container.css"
   "/js-ext/yui/build/calendar/assets/calendar.css"
   "/js-ext/yui/build/treeview/assets/treeview-core.css"
   "/js-ext/yui/build/resize/assets/skins/sam/resize.css"
   "/assets/i2b2.css"
   "/assets/i2b2-NEW.css"
   "/css/webclient.css"])


(defprocess header
  "Creates the default header that is used for the application when in an authenticated session."
  [request options]
  [:table#topBarTable {:border 0 :cellspacing 0 :cellpadding 0 :width"100%"}
   [:tr
    [:td {:align "left" :valign "middle"}
     [:img#topBarTitle {:src "/assets/images/title.gif" :border 0}]]
    [:td {:align "left" :valign "middle"}
     [:div#viewMode-Project]]
    [:td {:align "right" :valign "middle"}
     [:div#viewMode-User (:full-name (auth/get-user))]]
    [:td {:align "right" :valign "middle"}
     [:div#topBar
      (interpose "&nbsp;|&nbsp;" (map #([:a {:href (first %)} (second %)]) (:links options)))
      "&nbsp;|&nbsp;"
      [:a {:href "/logout"} "Logout"]]]]])

(defprocess header-no-session
  "Creates the default header that is used for the application when not in an authenticated session."
  [request options]
  [:table#topBarTable {:border 0 :cellspacing 0 :cellpadding 0 :width"100%"}
   [:tr
    [:td {:align "left" :valign "middle"}
     [:img#topBarTitle {:src "/assets/images/title.gif" :border 0}]]
    [:td {:align "right" :valign "middle"}
     [:div#topBar
      (interpose "&nbsp;|&nbsp;"
                 (map
                   (fn[link] [:a {:href (first link)} (second link)])
                   (:links options)))]]]])

(defprocess footer
  "Creates the default footer that is used for the application when in an authenticated session."
  [request options])

(defprocess footer-no-session
  "Creates the default footer that is used for the application when not in an authenticated session."
  [request options])

(defprocess content
  "Creates the default content block that is used for the application when in an authenticated session."
  [request options elements]
  [:div.content elements])

(defprocess content-no-session
  "Creates the default content block that is used for the application when in an authenticated session."
  [request options elements]
  [:div.content elements])

(defprocess page
  "Creates the default page layout that is used for the application when not in an authenticated session."
  [request options elements]
  (list (header request options)
        (content request options elements)
        (footer request options)))

(defprocess page-no-session
  "Creates the default page layout that is used for the application when not in an authenticated session."
  [request options elements]
  (list (header-no-session request options)
        (content-no-session request options elements)
        (footer-no-session request options)))

(defprocess body
  "Creates the default layout that is used for the application when in an authenticated session."
  [request options elements]
  [:body.yui-skin-sam (page request options elements)])

(defprocess body-no-session
  "Creates the default layout that is used for the application when not in an authenticated session."
  [request options elements]
  [:body.yui-skin-sam (page-no-session request options elements)])

(defprocess head
  "Creates the head section of the document when in an authenticated session."
  [request options]
  [:head [:title (or (:title options) "I2B2 Web Client")]
    (if (nil? (get-in request [:query-params :pageRequest]))
      (list
        (apply page/include-css (stylesheets request options))
        (apply page/include-js (scripts request options))))])

(defprocess head-no-session
  "Creates the head section of the document when not in an authenticated session."
  [request options]
  [:head [:title (or (:title options) "I2B2 Web Client")]
    (if (nil? (get-in request [:query-params :pageRequest]))
      (list
        (apply page/include-css (stylesheets request options))
        (apply page/include-js (scripts request options))))])

(defprocess page-layout
  "Generates the standard layout when in an authenticated session."
  [request options elements]
  (page/html5
    (head request options)
    (body request options elements)))

(defprocess page-layout-no-session
  "Generates the standard layout when not in an authenticated session."
  [request options elements]
  (page/html5
    (head-no-session request options)
    (body-no-session request options elements)))

(defprocess render-page
  "Decides how markup should be wrapped in a container.  This provides
   the ability to add additional containers later based on how the
   request was made.  IE - make into a portlet."
  [request options & elements]
  (page-layout request options elements))

(deflayer render-page render-page-no-session
  "Renders into a layout specific for responses that are outside of an authenticated session."
  [request options & elements]
  (if (not (auth/authenticated?))
    (page-layout-no-session request options elements)
    (continue)))
