const extensions = 'https://developer.chrome.com/docs/extensions';
const webstore = 'https://developer.chrome.com/docs/webstore';
const utcourseschedule = 'https://utdirect.utexas.edu/apps/registrar/course_schedule';
  

const classXMLFile = new XMLHttpRequest();
classXMLFile.open("GET", "test3.xml", false);
classXMLFile.send();
const classXMLDoc = classXMLFile.responseXML;

// const parserXML = new DOMParser();

// simple implemetation for testing. have map later on
function expandAbbrieviation(fieldOfStudy) {
    if (fieldOfStudy === "C S") {
        return "Computer Science"
    }
    return "testing";
}

function extractCourseName(courseNameFromWebsite) {
    var splitData = courseNameFromWebsite.split(/\s+/);
    let returnString = "";
    let fieldOfStudy = "";
    let courseNumber = "";

    // simple rule for now. fix later if need
    for (let i = 0; i < 3; i++) {
        if (splitData[i].length == 1) {
            fieldOfStudy += splitData[i] + ' ';
        } else if (splitData[i].length <= 4) {
            courseNumber += splitData[i];
        }
    }

    returnString = expandAbbrieviation(fieldOfStudy.substring(0, fieldOfStudy.length - 1)) + ' ' + courseNumber;
    
    return returnString;
}

const tabChar = "&nbsp; &nbsp; &nbsp; &nbsp;";
const newLineChar = "<br/>";

function printOneCourse(indent, courseID) {
    let returnString = "";

    let currCourse = classXMLDoc.getElementsByTagName("course")[courseID];
    let courseName = currCourse.getElementsByTagName("courseName")[0].innerHTML;
    let entireOutput = currCourse.getElementsByTagName("entireOutput")[0].innerHTML;

    returnString += indent + courseName + " - " + entireOutput + newLineChar;
    indent += tabChar;

    let prereqList = currCourse.getElementsByTagName("prereq");
    for (let i = 0; i < prereqList.length; i++) {
        let currPrereq = prereqList[i];
        let outputText = currPrereq.getElementsByTagName("output")[0].innerHTML;
        returnString += indent + (i + 1) + ". " + outputText + newLineChar;
        let prereqCourseList = currPrereq.getElementsByTagName("prereqCourses");
        for (let j = 0; j < prereqCourseList.length; j++) {
            let currPrereqCourse = prereqCourseList[j];
            let currID = currPrereqCourse.getElementsByTagName("nameAndID")[0].getElementsByTagName("nodeID")[0].innerHTML;
            returnString += printOneCourse(indent, currID);
        }
    }

    return returnString;
}

var courseMap = new Map();
const AND_VALUE = 0;
const OR_VALUE = 1;

function traverseCourseGraph(courseNode) {
    
}

function createPrereqGraph(courseID) {

    let currCourse = classXMLDoc.getElementsByTagName("course")[courseID];
    // I don't know if .innerHTML gets a string so i add string
    let courseName = currCourse.getElementsByTagName("courseName")[0].innerHTML + "";
    let entireOutput = currCourse.getElementsByTagName("entireOutput")[0].innerHTML + "";

    
    let returnNode;
    if (courseMap.get(courseID) == null) {
        returnNode = new CourseNode(courseID, courseName, entireOutput)
        courseMap.set(courseID, returnNode);
    } else {
        returnNode = courseMap.get(courseID);
    }

    let prereqList = currCourse.getElementsByTagName("prereq");
    // let ANDPrereqNode = new PrerequisiteNode(AND_VALUE, )
    for (let i = 0; i < prereqList.length; i++) {
        let currPrereq = prereqList[i];
        let outputText = currPrereq.getElementsByTagName("output")[0].innerHTML;

        let prereqNode = new PrerequisiteNode(OR_VALUE, outputText);
        console.log("here");
        returnNode.addPrereqNode(prereqNode);

        let prereqCourseList = currPrereq.getElementsByTagName("prereqCourses");
        for (let j = 0; j < prereqCourseList.length; j++) {
            let currPrereqCourse = prereqCourseList[j];
            let currID = currPrereqCourse.getElementsByTagName("nameAndID")[0].getElementsByTagName("nodeID")[0].innerHTML;
            prereqNode.addCourseNode(createPrereqGraph(currID));
        }
    }

    return returnNode;
}

class CourseNode {
    constructor(courseID, courseName, entireOutput) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.entireOutput = entireOutput;
        this.listOfPrereqNodes = [];
        console.log("created " + courseName + " node");
    }

    // get courseID() {
    //     return this.courseID;
    // }

    // get courseName() {
    //     return this.courseName;
    // }

    // get output() {
    //     return this.entireOutput;
    // }

    // set courseID(courseID) {
    //     this.courseID = courseID;
    // }

    // set courseName(courseName) {
    //     this.courseName = courseName;
    // }

    // set output(entireOutput) {
    //     this.entireOutput = entireOutput;
    // }

    addPrereqNode(prereqNode) {
        this.listOfPrereqNodes.push(prereqNode);
    }


}

class PrerequisiteNode {
    constructor(andOr, prereqOutput) {
        this.andOr = andOr;
        this.prereqOutput = prereqOutput;
        this.listOfCourses = [];
        console.log("created are or node");
    }

    addCourseNode(courseNode) {
        this.listOfCourses.push(courseNode);
    }
}

function createPrereqString(courseName) {
    console.log("enter createPrereq");
    let courseArray = classXMLDoc.getElementsByTagName("course");
    let courseID;
    console.log(courseArray.length);
    for (let i = 0; i < courseArray.length; i++) {
        let currCourse = courseArray[i];
        let currCourseName = currCourse.getElementsByTagName("courseName")[0].innerHTML;
        // console.log('help');
        if (currCourseName === courseName) {
            console.log("success");
            courseID = i;
            break;
        }
    }
    createPrereqGraph(courseID);
    return printOneCourse("", courseID);
}


//https://developer.chrome.com/docs/extensions/reference/tabs/
  //https://stackoverflow.com/questions/6132018/how-can-i-get-the-current-tab-url-for-chrome-extension
  chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
    // since only one tab should be active and in the current window at once
    // the return variable should only have one entry
    var activeTab = tabs[0];
    var activeTabId = activeTab.id; // or do whatever you need
    console.log(activeTab);

    if (activeTab.url.startsWith(utcourseschedule)) {
        console.log("we are here");

        let test = chrome.scripting.executeScript({
            target: { tabId: activeTabId },
            injectImmediately: true,  // uncomment this to make it execute straight away, other wise it will wait for document_idle
            func: DOMtoString,
            args: ['#details h2']  // you can use this to target what element to get the html for
           // https://stackoverflow.com/questions/18404211/queryselectorall-how-to-only-get-elements-within-a-certain-div
        }).then((results) => {
            var courseNameFromWebsite = results[0].result;
            document.getElementById("mainTitle").innerHTML = courseNameFromWebsite;
            let courseName = extractCourseName(courseNameFromWebsite);
            
            document.getElementById("prereqText").innerHTML = createPrereqString(courseName);
        });
        
        
        // console.log(classXMLDoc.getElementsByTagName("course")[0].innerHTML);
        
    } else {
        // chrome.action.setPopup({popup: "wrongpage.html"});
        // https://stackoverflow.com/questions/5016442/chrome-extension-refresh-popup-html-to-a-new-html-page
        window.location.href = "wrongpage.html";
    }
  });


  https://stackoverflow.com/questions/11684454/getting-the-source-html-of-the-current-page-from-chrome-extension
  function DOMtoString(selector) {
    if (selector) {
        selector = document.querySelector(selector);
        if (!selector) return "ERROR: querySelector failed to find node"
    } else {
        selector = document.documentElement;
    }
    return selector.innerHTML;
}
