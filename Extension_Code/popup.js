{/* <script type="module">
import cytoscape from "./cytoscape.esm.min.js";
</script> */}

var cytoscape = require('cytoscape');
var dagre = require('cytoscape-dagre');
cytoscape.use(dagre);


const extensions = 'https://developer.chrome.com/docs/extensions';
const webstore = 'https://developer.chrome.com/docs/webstore';
const utcourseschedule = 'https://utdirect.utexas.edu/apps/registrar/course_schedule';
const XMLFileName = "test15.xml";

const classXMLFile = new XMLHttpRequest();
classXMLFile.open("GET", XMLFileName, false);
classXMLFile.send();
const classXMLDoc = classXMLFile.responseXML;

// const parserXML = new DOMParser();

// simple implemetation for testing. have map later on
function expandAbbrieviation(fieldOfStudy) {
    if (fieldOfStudy === "C S") {
        return "Computer Science";
    } else if (fieldOfStudy === "M") {
        return "Mathematics";
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

// function printOneCourse(indent, courseID) {
//     let returnString = "";

//     let currCourse = classXMLDoc.getElementsByTagName("course")[courseID];
//     let courseName = currCourse.getElementsByTagName("courseName")[0].innerHTML;
//     let entireOutput = currCourse.getElementsByTagName("entireOutput")[0].innerHTML;

//     returnString += indent + courseName + " - " + entireOutput + newLineChar;
//     indent += tabChar;

//     let prereqList = currCourse.getElementsByTagName("prereq");
//     for (let i = 0; i < prereqList.length; i++) {
//         let currPrereq = prereqList[i];
//         let outputText = currPrereq.getElementsByTagName("output")[0].innerHTML;
//         returnString += indent + (i + 1) + ". " + outputText + newLineChar;
//         let prereqCourseList = currPrereq.getElementsByTagName("prereqCourses");
//         for (let j = 0; j < prereqCourseList.length; j++) {
//             let currPrereqCourse = prereqCourseList[j];
//             let currID = currPrereqCourse.getElementsByTagName("nameAndID")[0].getElementsByTagName("nodeID")[0].innerHTML;
//             returnString += printOneCourse(indent, currID);
//         }
//     }

//     return returnString;
// }

var courseMap = new Map();
const AND_VALUE = 0;
const OR_VALUE = 1;

function traverseCourseGraph(courseNode) {
    
}

function testingCourseGraphTraversal(currNode, parentString) {
    console.log(parentString + ": " + currNode.toString());
    for (let i = 0; i < currNode.listOfNodes.length; i++) {
        testingCourseGraphTraversal(currNode.listOfNodes[i], parentString + ": " + currNode.toString());
    }
}

function displayGraph(rootCourseNode) {
    var cy = cytoscape({
        container: document.getElementById('cy'),

        style: [ // the stylesheet for the graph
        
        
        {
            selector: 'node',
            style: {
                'background-color': '#000',
                'label': 'data(displayName)'
            }
        },

        {
            selector: '.andOr',
            style: {
                'background-color': '#FFF',
                // 'opacity': 0,
                'width': 1,
                'height': 1,
                'label': ''
            }
        },

        {
            selector: 'edge',
            style: {
                'width': 3,
                'line-color': '#ccc',
                'target-arrow-color': '#ccc',
                'target-arrow-shape': 'triangle',
                'curve-style': 'bezier'
            }
        }

        
    ],
    zoomingEnabled: true
    });

    addNodesToDisplay(rootCourseNode, cy, "");
    addEdgesToDisplay(rootCourseNode, cy);

    let options = {
        name: 'breadthfirst',
      
        fit: true, // whether to fit the viewport to the graph
        directed: false, // whether the tree is directed downwards (or edges can point in any direction if false)
        padding: 30, // padding on fit
        circle: false, // put depths in concentric circles if true, put depths top down if false
        grid: false, // whether to create an even grid into which the DAG is placed (circle:false only)
        spacingFactor: 1.75, // positive spacing factor, larger => more space between nodes (N.B. n/a if causes overlap)
        boundingBox: undefined, // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
        avoidOverlap: true, // prevents node overlap, may overflow boundingBox if not enough space
        nodeDimensionsIncludeLabels: false, // Excludes the label when calculating node bounding boxes for the layout algorithm
        roots: undefined, // the roots of the trees
        depthSort: undefined, // a sorting function to order nodes at equal depth. e.g. function(a, b){ return a.data('weight') - b.data('weight') }
        animate: false, // whether to transition the node positions
        animationDuration: 500, // duration of animation in ms if enabled
        animationEasing: undefined, // easing of animation if enabled,
        animateFilter: function ( node, i ){ return true; }, // a function that determines whether the node should be animated.  All nodes animated by default on animate enabled.  Non-animated nodes are positioned immediately when the layout starts
        ready: undefined, // callback on layoutready
        stop: undefined, // callback on layoutstop
        transform: function (node, position ){ return position; } // transform a given node position. Useful for changing flow direction in discrete layouts
      };

      var defaults = {
        name: 'dagre',
        // dagre algo options, uses default value on undefined
        nodeSep: undefined, // the separation between adjacent nodes in the same rank
        edgeSep: undefined, // the separation between adjacent edges in the same rank
        rankSep: undefined, // the separation between each rank in the layout
        rankDir: undefined, // 'TB' for top to bottom flow, 'LR' for left to right,
        align: undefined,  // alignment for rank nodes. Can be 'UL', 'UR', 'DL', or 'DR', where U = up, D = down, L = left, and R = right
        acyclicer: undefined, // If set to 'greedy', uses a greedy heuristic for finding a feedback arc set for a graph.
                              // A feedback arc set is a set of edges that can be removed to make a graph acyclic.
        ranker: undefined, // Type of algorithm to assign a rank to each node in the input graph. Possible values: 'network-simplex', 'tight-tree' or 'longest-path'
        minLen: function( edge ){ return 1; }, // number of ranks to keep between the source and target of the edge
        edgeWeight: function( edge ){ return 1; }, // higher weight edges are generally made shorter and straighter than lower weight edges
      
        // general layout options
        fit: true, // whether to fit to viewport
        padding: 30, // fit padding
        spacingFactor: 2, // Applies a multiplicative factor (>0) to expand or compress the overall area that the nodes take up
        nodeDimensionsIncludeLabels: false, // whether labels should be included in determining the space used by a node
        animate: false, // whether to transition the node positions
        animateFilter: function( node, i ){ return true; }, // whether to animate specific nodes when animation is on; non-animated nodes immediately go to their final positions
        animationDuration: 500, // duration of animation in ms if enabled
        animationEasing: undefined, // easing of animation if enabled
        boundingBox: undefined, // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
        transform: function( node, pos ){ return pos; }, // a function that applies a transform to the final node position
        ready: function(){}, // on layoutready
        sort: undefined, // a sorting function to order the nodes and edges; e.g. function(a, b){ return a.data('weight') - b.data('weight') }
                         // because cytoscape dagre creates a directed graph, and directed graphs use the node order as a tie breaker when
                         // defining the topology of a graph, this sort function can help ensure the correct order of the nodes/edges.
                         // this feature is most useful when adding and removing the same nodes and edges multiple times in a graph.
        stop: function(){} // on layoutstop
      };

      var layout = cy.layout(defaults);
      layout.run();
}

var edgeCounter = 0;

function addEdgesToDisplay(currentNode, cy) {
    if (currentNode.edgesToThisNodeCreated === true) {
        console.log("edges to this node already created");
        return;
    } else {
        currentNode.edgesToThisNodeCreated = true;
    }

    for (let i = 0; i < currentNode.listOfNodes.length; i++) {
        let currChild = currentNode.listOfNodes[i];
        let edgeID = "edge" + edgeCounter++;
        let sourceID = currChild.nodeID;
        let targetID = currentNode.nodeID;
        cy.add({
            group: 'edges', 
            
            data: { 
                id: edgeID, 
                source: sourceID, 
                target: targetID 
            }
        })
        addEdgesToDisplay(currChild, cy);
    }

}

var ANDCounter = 0;
var ORCounter = 0;

function addNodesToDisplay(currentNode, cy, prevID) {
    // don't create multiple of the same node
    if (currentNode.createdDisplayNode === true) {
        console.log("display node already created");
        return;
    } else {
        currentNode.createdDisplayNode = true;
    }
    

    var nodeDisplayName = currentNode.toString();
    var nodeID = nodeDisplayName;
    var className = nodeDisplayName;
    // console.log("className is " + className);
    // console.log(typeof className);
    // console.log(typeof "AND");
    if (className === "AND") {
        className = 'andOr';
        console.log("is an and node");
        nodeID = "AND" + ANDCounter++;
    } else if (className === "OR") {
        className = 'andOr';
        console.log("is an or node");
        nodeID = "OR" + ORCounter++;
    }
    
    // idk why this didn't work
    // if (className === "AND" || className === "OR") {
    //     console.log("and or node");
    //     console.log(className === "AND");
        
    //     console.log(className === "OR");
    //     className = 'andOr';
    //     if (className === "AND") {
    //     } else if (className === "OR") {

            
    //     }
    // }
    console.log("creating display with nodeID: " + nodeID);
    currentNode.nodeID = nodeID;
    cy.add({
        group: 'nodes',

        data: {
            id: nodeID,
            displayName: nodeDisplayName
        },

        position: {
            x: 50,
            y: 50
        },

        scratch: {
        },

        classes: [className]
    })

    for (let i = 0; i < currentNode.listOfNodes.length; i++) {
        let nextNode = currentNode.listOfNodes[i];
        addNodesToDisplay(nextNode, cy, nodeID);
    }
}

var listOfNoPrereqStatements = ["NO PREREQUISITES", "Class wasn't found in scraping."];
function matchesPrereqStatement(givenString) {
    for (let i = 0; i < listOfNoPrereqStatements.length; i++) {
        let currString = listOfNoPrereqStatements[i];
        if (givenString === currString) {
            return true;
        }
    }
    return false;
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
        return returnNode;
    }
    
    let prereqList = currCourse.getElementsByTagName("prereq");
    // if (courseName === "An appropriate score on the mathematics placement exam.") {
    //     console.log("length of appropriate score prereqlist is " + prereqList.length);
    // }
    // don't create and/or nodes if there are no prerequisites
    if (!matchesPrereqStatement(entireOutput)) {
        // let andOutputText = currCourse.getElementsByTagName("entireOutput")[0].innerHTML;
        let ANDPrereqNode = returnNode;
        // let ANDPrereqNode = new PrerequisiteNode(AND_VALUE, andOutputText);
        // returnNode.addPrereqNode(ANDPrereqNode);
        for (let i = 0; i < prereqList.length; i++) {
            let currPrereq = prereqList[i];
            let orOutputText = currPrereq.getElementsByTagName("output")[0].innerHTML;

            let orPrereqNode = new PrerequisiteNode(OR_VALUE, orOutputText);
            // console.log("here");
            ANDPrereqNode.addNode(orPrereqNode);

            let prereqCourseList = currPrereq.getElementsByTagName("prereqCourses");
            for (let j = 0; j < prereqCourseList.length; j++) {
                let currPrereqCourse = prereqCourseList[j];
                let currID = currPrereqCourse.getElementsByTagName("nameAndID")[0].getElementsByTagName("nodeID")[0].innerHTML;
                orPrereqNode.addNode(createPrereqGraph(currID));
            }
        }
    }

    return returnNode;
}

class CourseNode {
    constructor(courseID, courseName, entireOutput) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.entireOutput = entireOutput;
        this.listOfNodes = [];
        this.createdDisplayNode = false;
        this.nodeID = "";
        this.edgesToThisNodeCreated = false;
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

    addNode(node) {
        this.listOfNodes.push(node);
    }

    addPrereqNode(prereqNode) {
        this.listOfNodes.push(prereqNode);
    }

    toString() {
        return this.courseName + "";
    }


}

class PrerequisiteNode {
    constructor(andOr, prereqOutput) {
        this.andOr = andOr;
        this.prereqOutput = prereqOutput;
        this.listOfNodes = [];
        this.createdDisplayNode = false;
        this.nodeID = "";
        this.edgesToThisNodeCreated = false;
        console.log("created are or node");
    }

    addNode(node) {
        this.listOfNodes.push(node);
    }

    addCourseNode(courseNode) {
        this.listOfNodes.push(courseNode);
    }

    toString() {
        if (this.andOr === AND_VALUE) {
            return "AND";
        }
        return "OR";
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
    let prereqGraphRoot = createPrereqGraph(courseID);
    testingCourseGraphTraversal(prereqGraphRoot, "start");
    displayGraph(prereqGraphRoot);
    return "code was ran";
}

// function createPrereqString(courseName) {
//     console.log("enter createPrereq");
//     let courseArray = classXMLDoc.getElementsByTagName("course");
//     let courseID;
//     console.log(courseArray.length);
//     for (let i = 0; i < courseArray.length; i++) {
//         let currCourse = courseArray[i];
//         let currCourseName = currCourse.getElementsByTagName("courseName")[0].innerHTML;
//         // console.log('help');
//         if (currCourseName === courseName) {
//             console.log("success");
//             courseID = i;
//             break;
//         }
//     }
//     createPrereqGraph(courseID);
//     return printOneCourse("", courseID);
// }


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

            // var cy = cytoscape({

            //     container: document.getElementById('cy'), // container to render in
              
            //     elements: [ // list of graph elements to start with
            //       { // node a
            //         data: { id: 'a' }
            //       },
            //       { // node b
            //         data: { id: 'b' }
            //       },
            //       { // edge ab
            //         data: { id: 'ab', source: 'a', target: 'b' }
            //       }
            //     ],
              
            //     style: [ // the stylesheet for the graph
            //       {
            //         selector: 'node',
            //         style: {
            //           'background-color': '#666',
            //           'label': 'data(id)'
            //         }
            //       },
              
            //       {
            //         selector: 'edge',
            //         style: {
            //           'width': 3,
            //           'line-color': '#ccc',
            //           'target-arrow-color': '#ccc',
            //           'target-arrow-shape': 'triangle',
            //           'curve-style': 'bezier'
            //         }
            //       }
            //     ],
              
            //     layout: {
            //       name: 'grid',
            //       rows: 1
            //     }
              
            //   });
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
