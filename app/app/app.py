from flask import Flask, render_template
from datetime import datetime
from flask import request, jsonify, abort
import pyodbc
import json
from pathlib import Path

app = Flask(__name__)

def connection():
    conn_str = (
    r'Driver=ODBC Driver 17 for SQL Server'
    r'Server=.\DESKTOP-4T81S99;'
    r'Database=SearchEngine;'
    r'Trusted_Connection=yes;'
    )
    cnxn = pyodbc.connect("Driver={ODBC Driver 17 for SQL Server};"
    "Server=DESKTOP-4T81S99;"
    "Database=SearchEngine;"
    "Trusted_Connection=yes;")
    return cnxn

def format(url):
        return {
            "id" : url["id"], 
            "url" : url["url"], 
            
        }

@app.route("/results", methods=['POST'])
def post_req():
    textBox=request.form['searchBox']
    newText=textBox.lower();
    fo = open("test.txt","wb")
    fo.write(newText.encode())
    path_file = "C:\\Users\\saiko\\OneDrive\\Desktop\\Search-Engine\\springboot-first-app\\linksResults"

    with open(path_file, 'r') as f:        
        links_list = [line.strip() for line in f]
    totalPages = len(links_list)/20
    return render_template("results.html",totalPages = totalPages, newText = newText,  links_list = links_list)
    
@app.route("/", methods=['GET'])
def get_home():
    return render_template("homepage.html")

@app.route("/results", methods=['GET'])
def get_all_req():
    links = []
    conn = connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM dbo.Link")
    for row in cursor.fetchall():
        links.append({"id": row[0], "url": row[1]})
    
    response = {
            'Links':
            [format(link) for link in links]}
    conn.close()
    return response


if(__name__ == "__main__"):
    app.run()


