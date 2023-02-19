import vibe.vibe;
import virus_total;
import db_conn;

import std.stdio;

void main()
{

    auto dbClient = DBConnection("root", "example", "mongo", "27017", "testing");
    auto virusTotalAPI = new VirusTotalAPI(dbClient);

    auto router = new URLRouter;
    router.registerRestInterface(virusTotalAPI);

    router.get("*", serveStaticFiles("public/"));
    router.get("/home", &home_page);
    router.get("/register", &register_page);
    router.get("/login", &login_page);
    router.get("/auth", &auth_page);
    router.get("/delete", &delete_page);

    router.get("/urls", &urls_page);
    router.get("/add_url", &add_url_page);
    router.get("/url_info", &url_info_page);
    router.get("/user_urls", &user_urls_page);
    router.get("/delete_url", &delete_url_page);

    router.get("/files", &files_page);
    router.get("/add_file", &add_file_page);
    router.get("/file_info", &file_info_page);
    router.get("/user_files", &user_files_page);
    router.get("/delete_file", &delete_file_page);

    auto settings = new HTTPServerSettings;
    settings.port = 8080;
    settings.bindAddresses = ["0.0.0.0"];


    auto listener = listenHTTP(settings, router);
    scope (exit)
    {
        listener.stopListening();
    }

    writeln(router.getAllRoutes());
    runApplication();
}

void home_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("home_page.dt")(res);
}

void register_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("register_page.dt")(res);
}

void login_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("login_page.dt")(res);
}

void auth_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("auth_page.dt")(res);
}

void delete_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("delete_page.dt")(res);
}

void add_url_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("add_url_page.dt")(res);
}

void url_info_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("url_info_page.dt")(res);
}

void user_urls_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("user_urls_page.dt")(res);
}

void delete_url_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("delete_url_page.dt")(res);
}

void urls_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("urls_page.dt")(res);
}

void add_file_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("add_file_page.dt")(res);
}

void file_info_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("file_info_page.dt")(res);
}

void user_files_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("user_files_page.dt")(res);
}

void delete_file_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("delete_file_page.dt")(res);
}

void files_page(HTTPServerRequest req, HTTPServerResponse res)
{
    
    render!("files_page.dt")(res);
}



