# Ilmo - a simple training registration tool

![preview](http://github.com/Solita/Ilmo/raw/master/ui.png)

## How to run

### Localhost / development
Command "sbt ~container:start" will start Ilmo on port 9999. By default Ilmo runs with the h2
database. Data is saved on a (.db)-file in the project dir. To reload code changes without reboot, 
run "sbt", "container:start", "~container:reload /"

### External servlet container and database.   
Create a database user named ilmo. Set up properties in production.default.props.
```bash
# Assuming production.default.props contains db.createschema=true. Create the tables with:
sbt -Drun.mode=production container:start

# compile the app with 
sbt package-war

# Then just copy the resulting war in a servlet container. Tested with jetty6. Don't forget the run.mode sysprop.
```

## License

Distributed under the MIT License.
