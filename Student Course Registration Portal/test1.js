var data={"Machine Learning":[["mlt1", "mlt2"], ["l1", "l2"]], "Software Development":[["sdt1", "sdt2", "sdt3"], ["l2", "l3", "l4"]]};
var teachers={"mlt1":"l1", "mlt2":"l2", "sdt1":"l2", "sdt2":"l3", "sdt3":"l4"};

var fs=require('fs');
var editJsonFile=require('edit-json-file')
var pug=require('pug');

var bodyParser = require('body-parser');
var express = require('express')
var app = express()
var multer  = require('multer')
var upload = multer()
app.set('view engine', 'pug');
app.set('views', './');

app.get('/', function(req, res){
    res.render('form');
 });
 


var studname;
var studreg;
app.post('/student',upload.none(), function(req, res)
{
    studname=req.body.name;
    studreg=req.body.reg;
    res.render('menu.pug');
});
app.post('/nextStep', upload.none(), function(req, res)
{
    res.render('menu.pug');
});
function readFile(filename, promise) {
    var promise=new Promise((resolve, reject)=>
    {
        fs.readFile(filename,(err, data)=>
        {
            if(err)
            return(reject(err));
            resolve(data); //executes the then function
        })
    })
    return(promise);
}
var subject;
app.post('/student/:course', upload.none(),function(req, res)
{
    subject=req.params.course;
    if(subject=="ml")
    {
        var promise=readFile("ml.json");
        promise
            .then((data)=>{var obj=JSON.parse(data); 
                var a=obj["teachers"]; var b=obj["slots"]; var c=obj["currentSeats"];
                var arr=[];
                for(var i=0;i<a.length;i++)
                {
                    arr.push(a[i]);
                    arr.push("-");
                    arr.push(b[i]);
                    arr.push("-");
                    arr.push(c[i]);
                    arr.push(" | ");
                }
                var s=arr.join("");
             res.render('displaySlots.pug', {data1:s})})
            .catch((err)=>console.log(err));
        
    }
    else
    {
        var promise=readFile("sd.json");
        promise
            .then((data)=>{var obj=JSON.parse(data); 
                var a=obj["teachers"]; var b=obj["slots"]; var c=obj["currentSeats"];
                var arr=[];
                for(var i=0;i<a.length;i++)
                {
                    arr.push(a[i]);
                    arr.push("-");
                    arr.push(b[i]);
                    arr.push("-");
                    arr.push(c[i]);
                    arr.push(" | ");
                }
                var s=arr.join("");
             res.render('displaySlots.pug', {data1:s})})
            .catch((err)=>console.log(err));
        
    }
    //res.write("You have selected "+ subject);
    //res.end();
});
var teacher;
app.post('/student/reg/add',upload.none(), function(req, res)
{
    teacher=req.body.teacher;
    //console.log(teacher);
    
    var file=editJsonFile("students.json");
    
        var arr=file.get(studreg);
        var flag=1;
        for(var i=0;i<arr.length;i++)
        {
            if(arr[i][0]==subject)
            {
                flag=0;
                res.render('navigation.pug', {msge:"You have already registered for this course!"});
                break;
            }
            if(arr[i][2]==teachers[teacher])
            {
                flag=0;
                res.render('navigation.pug', {msge:"You already have a course registered in this slot!"});
                break;
            }  
        }
        if(flag==1 && subject=='sd')
        {
            var promise=readFile("sd.json");
            promise
                .then((data)=>{var obj=JSON.parse(data); 
                    var c=obj["currentSeats"];
                    if(c==0)
                    {
                        flag=0;
                        res.render('navigation.pug', {msge:"Sorry, all the seats have been filled in this course!"});
                    }
                 })
                .catch((err)=>console.log(err));   
        }
        if(flag==1 && subject=='ml')
        {
            var promise=readFile("ml.json");
            promise
                .then((data)=>{var obj=JSON.parse(data); 
                    var c=obj["currentSeats"];
                    if(c==0)
                    {
                        flag=0;
                        res.render('navigation.pug', {msge:"Sorry, all the seats have been filled in this course!"});
                    }
                 })
                .catch((err)=>console.log(err));  
        }
        if(flag==1)
        {
            arr.push([subject, teacher, teachers[teacher]]);
            file.set(studreg, arr);
            file.save();
            file = editJsonFile('students.json', {
            autosave: true
            });
            if(subject=='sd')
            {
                var file1=editJsonFile("sd.json");
                var x=file1.get("currentSeats");
                var y=file1.get("teachers");
                for(var i=0;i<y.length;i++)
                {
                    if(y[i]==teacher)
                    {
                        x[i]=x[i]-1;
                        break;
                    }
                }
                file1.set("curentSeats", x);
                file1.save();
                file1 = editJsonFile('sd.json', {
                autosave: true
                });

            }
            if(subject=='ml')
            {
                var file1=editJsonFile("ml.json");
                var x=file1.get("currentSeats");
                var y=file1.get("teachers");
                for(var i=0;i<y.length;i++)
                {
                    if(y[i]==teacher)
                    {
                        x[i]=x[i]-1;
                        break;
                    }
                }
                file1.set("curentSeats", x);
                file1.save();
                file1 = editJsonFile('ml.json', {
                autosave: true
                });

            }
            res.render('navigation.pug', {msge:"Successfully registered for this course!"});
        }
        
    //res.end();
});
app.post('/nextStep/:nav', upload.none(), function(req, res)
{
    if(req.params.nav=="menu")
    res.render("menu.pug");
    if(req.params.nav=="quit")
    res.send("Thank you "+studname+"!");
});
app.post('/action/:act', upload.none(), function(req, res)
{
    var todo=req.params.act;
    if(todo=="add")
    {
        res.render('courses.pug');
    }
    if(todo=="delete")
    {
        var file=editJsonFile("students.json");
        var arr=file.get(studreg);
        var ans="";
        for(var i=0;i<arr.length;i++)
        {
            var subarr=arr[i];
            if(subarr[0]=="ml")
            ans=ans+"Machine Learning   |   ";
            if(subarr[0]=="sd")
            ans=ans+"Software Development   |  ";
        }
        res.render("removeCourse.pug", {added:ans});

    }
    if(todo="view")
    {
        //var file=editJsonFile("students.json");
        var file= editJsonFile('students.json', {
            autosave: true
            });
        var arr=file.get(studreg);
        var ans="";
        for(var i=0;i<arr.length;i++)
        {
            var subarr=arr[i];
            if(subarr[0]=="ml")
            ans=ans+"Machine Learning   |   ";
            if(subarr[0]=="sd")
            ans=ans+"Software Development   |  ";
        }
        res.render("viewCourses.pug", {toView:ans});
    }
});
app.post('/remove', upload.none(), function(req, res)
{
    var toRemove=req.body.subj;
    var file=editJsonFile("students.json");
    var arr=file.get(studreg);
    var newarr=[];
    for(var i=0;i<arr.length;i++)
    {
        var subarr=arr[i];
        if(subarr[0]!=toRemove)
        {
            newarr.push(subarr);
        }
    }
    file.set(studreg, newarr);
        file.save();
        file = editJsonFile('students.json', {
            autosave: true
        });
    res.render('navigation.pug', {msge:"Successfully deleted this course!"});
    
} );
app.listen(1234);
