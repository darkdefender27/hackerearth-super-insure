from flask import Flask
app = Flask(__name__)

@app.route('/fico')
def index():
    return "Hello, World!"

if __name__ =="__main__":
    try:
        app.run(debug=True)
    except KeyboardInterrupt:
        raise
