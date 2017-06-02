
OdooConnect is a helper to access Odoo 10 servers from Android devices.
==========================================================================

You can modify it and add extra functionality, if needed for your apps.

This class is based on [OpenErpConnect](https://github.com/zikzakmedia/android-openerp) (for old version of Odoo) which uses the [android-xmlrpc](http://code.google.com/p/android-xmlrpc/) connector.
In this case I use the [aXMLRPC](https://github.com/gturri/aXMLRPC/tree/master) connector which is more easy to implement.

To connect from Android to Odoo you will need to use android AsyncTask. In my case, I have not achieved it in any other way.
[Here is an app example.](https://github.com/Edu095/OdooConnect-Android/tree/OdooConnect-App)

## Here are some features:
(For more information see the source and its comments)

### Test Connection
Try connection to server. Return true if the connection is successful.
```
try {

    Boolean ocT = OdooConnect.testConnection("url_server", port,
            "name_database", "user_name", "password");

} catch (Exception ex) {
    System.out.println("Error: " + ex);
}
```

### Search / Read
In this case returns a name/phone list from model res.partner where contacts are Customer but not Company.
In case there was any field related to another model we could also obtain such data.
```
try {
    OdooConnect oc = OdooConnect.connect("url", port, "db", "username", "password");

    Object[] param = {new Object[]{
                    new Object[]{"customer", "=", true},
                    new Object[]{"is_company", "=", false}}};

    List<HashMap<String, Object>> data = oc.search_read("res.partner", param, "name", "phone");

    String msgResult = "";
    for (int i = 0; i < data.size(); ++i) {
        msgResult += "\n" + data.get(i).get("name");
    }
    System.out.println(msgResult);

} catch (Exception ex) {
    System.out.println("Error: " + ex);
}
```

### Search Count
Returns the total number of contacts.
```
try {
    OdooConnect oc = OdooConnect.connect("url", port, "db", "username", "password");

    Object[] param = {new Object[0]{}};
    Integer ids = oc.search_count("res.partner", param);
    System.out.println("Num. of customers: " + ids.toString() + "\n");

} catch (Exception ex) {
    System.out.println("Error: " + ex);
}
```

### Create
Creates a new record for the model res.partner.
In this case create a new Company and returns his id.
```
try {
    OdooConnect oc = OdooConnect.connect("url", port, "db", "username", "password");

    @SuppressWarnings("unchecked")
    Integer idC = oc.create("res.partner", new HashMap() {{
        put("name", "name_company");
        put("phone", "contact_phone");
        put("is_company", True);
    }});
    msgResult = idC.toString();
    System.out.println(idC.toString());

} catch (Exception ex) {
    System.out.println("Error: " + ex);

}
```

### Write
Modify an existing record. Returns true if the action is successful.
```
try {

    OdooConnect oc = OdooConnect.connect("url", port, "db", "username", "password");

    Boolean idW = oc.write("res.partner", new Object[]{ id_record },
      new HashMap() {{
          put("mail", "new_mail");
          put("phone", "new_phone");
      }});

} catch (Exception ex) {
    System.out.println("Error: " + ex);
}
```

### Unlink
Delete an existing record. Return true if the record is deleted.
```
try {

    OdooConnect oc = OdooConnect.connect("url", port, "db", "username", "password");

    Boolean idW = oc.write("res.partner", new Object[]{ id_record });

} catch (Exception ex) {
    System.out.println("Error: " + ex);
}
```

### Call
This is a generic method to call any action.
```
try {

    OdooConnect oc = OdooConnect.connect("url", port, "db", "username", "password");

    Object[] param = {new Object[]{
                    new Object[]{"customer", "=", false}};
    Object[] fields = {new Object[]{"name", "mail", "phone"};

    Object[] idW = oc.call("res.partner", "search", 0, 15, param, fields);

} catch (Exception ex) {
    System.out.println("Error: " + ex);
}
```
