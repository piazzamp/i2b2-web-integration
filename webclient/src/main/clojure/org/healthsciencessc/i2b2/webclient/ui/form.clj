(ns org.healthsciencessc.i2b2.webclient.ui.form)


(defn form
  "Create a form container with the option of halting a normal submit."
  [{:keys [url method submit] :as options} & items]
  [:form {:action url 
          :method method
          :onsubmit (if submit nil "return false;")} items])

(defn input-wrapper
  [field & items]
  (if (:contain field)
    [:div items]
    items))

(defn input-id
  ([field given-name] (input-id field given-name nil))
  ([field given-name suffix] 
    (str (name given-name) (:suffix field) suffix)))


(defn input-hidden
  "Generates a hidden input."
  [{:keys [value name label] :as field}]
  (let [id (input-id field name)]
    [:input {:id id :name name :value value :type "hidden"}]))


(defn input
  "Generates an input of the specified type."
  [input-type {:keys [value name label placeholder autofocus attributes] :as field}]
  (let [id (input-id field name)]
    (input-wrapper 
      field 
      (if label [:label {:for id} label])
      [:input (merge {:id id :name name :value value :type input-type :placeholder placeholder :autofocus autofocus} attributes)])))

(defn input-radio
  "Generates a radio button."
  [{:keys [items name label autofocus] :as field}]
  (input-wrapper field
                 [:fieldset
                  [:legend label]
                  (doall 
                    (map-indexed 
                      (fn [idx {:keys [value label] :as item}]
                        (let [id (input-id field name idx)
                              checked (= value (:value field))
                              attributes {:id id :name name :value value :type "radio" 
                                          :autofocus (and checked autofocus) :checked checked}]
                          (list
                            [:input attributes]
                            [:label {:for id} label]))) items))]))
(defn checkbox-value
  [value]
  (cond
    (true? value) "true"
    (false? value) "false"
    :else value))

(defn input-checkbox
  "Generates a checkbox control."
  [{:keys [name label value checked-value unchecked-value autofocus] :as field}]
  (let [id (input-id field name)
        attributes {:id id
                    :type :checkbox 
                    :name name
                    :checked (= value checked-value)
                    :autofocus autofocus
                    :data-checked-value (checkbox-value checked-value)
                    :data-unchecked-value (checkbox-value unchecked-value)}]
    (input-wrapper field
                   [:input attributes]
                   [:label {:for id} label])))

(defn selected
  [field value attributes]
  (if (= value (:value field))
    (merge attributes {:selected "true"})
    attributes))

(defn select-one
  "Generates a select control."
  [{:keys [items value name blank label autofocus] :as field}]
  (let [id (input-id field name)]
    (input-wrapper 
      field 
      [:label.select {:for id} label]
      [:select {:id id :name name :value value :autofocus autofocus}
       (if blank [:option (selected field nil {:value ""}) ""])
       (for [{:keys [value label] :as item} items]
         [:option (selected field value {:value value}) label])])))


(defn custom-checklist
  "Generates a list of checkable items to allow for many values in a single field."
  [{:keys [items name label] :as field}]
  (let [id (input-id field name)]
    (input-wrapper 
      field
      [:fieldset.custom-input {:data-role "controlgroup"
                               :data-name (clojure.core/name name)
                               :data-type "checklist"}
       [:legend label]
       (for [{:keys [value label checked disabled] :as item} items]
         (input-checkbox {:name (str (clojure.core/name name) "_" value)
                          :label label
                          :checked-value value
                          :checked checked
                          :disabled disabled
                          :within-custom "true"}))])))


(defmulti wrap-fields (fn [w fs] (:wrapper w)))

(defmethod wrap-fields :controlgroup
  [wrapper fields]
  (input-wrapper wrapper [:fieldset fields]))

;; Define the generic edit-field methods
(defmulti edit-field :type)

(defmethod edit-field :hidden
  [field]
  (input-hidden field))

(defmethod edit-field :username
  [field]
  (input "text" field))

(defmethod edit-field :password
  [field]
  (input "password" field))

(defmethod edit-field :date
  [field]
  (input "date" field))

(defmethod edit-field :number
  [field]
  (if-let [pattern (:pattern field)]
    (input "number" (assoc field :pattern pattern))
    (input "number" (assoc field :pattern "[0-9]*"))))

(defmethod edit-field :radio
  [field]
  (input-radio field))

(defmethod edit-field :checkbox
  [field]
  (input-checkbox field))

(defmethod edit-field :select-one
  [field]
  (select-one field))

(defmethod edit-field :checklist
  [field]
  (custom-checklist field))

(defmethod edit-field :default
  [field]
  (input "text" field))

(defn record->editable-field
  "Takes an arbitrary record (map) and
  a list of maps with name, label, and optional type"
  [record {field-kw :name
           field-type :type
           parser :parser
           :as field}]
  (let [field-val (get record field-kw)
        field-val (if parser (parser field-val) field-val)
        field-type (or field-type :text)]
    (edit-field (assoc field
                       :value field-val
                       :type field-type))))

(defn render-fields
  ([options fields] 
    (render-fields options fields {}))
  ([options fields record]
    (render-fields (dissoc options :fields) fields record (or (:fields options) {})))
  ([options fields record field-mods]
    (map #(let [field-name (:name %)
                field-options (field-mods field-name)]
            (if (:wrapper %)
              (wrap-fields % (render-fields options (:fields %) record field-mods))
              (record->editable-field record (merge options % field-options)))) fields)))
