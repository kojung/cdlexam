(ns cdlexam.db
  (:use cdlexam.template
        hiccup.core
        clojure.contrib.logging)
  (:require [appengine-magic.core :as ae]
            [appengine-magic.services.datastore :as ae-ds]
            [appengine-magic.services.user :as ae-user]
            [ring.util.response :as ring-resp]))

(def knowledge-db
  [["CDL medical certificates must be renewed every:"
    ["year"
     "two years"
     "four years"]
    1]
   
   ["Merging onto a road is safest if you:"
    ["inch over into the nearest lane so that other vehicles will give you room"
     "gain speed on the shoulder and merge"
     "wait for a large enough gap in traffic to enter the road"]
    2]
   
   ["What three things add up to the total stopping distance for your truck or bus?"
    ["Attention distance, reaction distance, slowing distance"
     "Observation distance, response distance, braking distance"
     "Perception distance, reaction distance, braking distance"]
    2]

   ["You are checking your steering and exhaust systems in a pre-trip inspection.  Which of these problems, if found, should be fixed before the vehicle is driven?"
    ["Oil on the tie rod"
     "Black smoke from the exhaust pipe"
     "Steering wheel play of more than 10 degrees (2 inches on a 20-inch steering wheel)"]
    2]

   ["How does vehicle weight affect stopping?"
    ["Empty trucks can take longer to stop than if loaded, but this is not normally the case for buses."
     "It doesn't, brakes work the same no matter the weight of the vehicle for both trucks and buses"
     "Fully loaded trucks take longer to stop, but buses loaded with passengers take less distance"]
    1]

   ["When the road are slippery, you should:"
    ["make turns as carefully as possible"
     "stop and test the traction while going up hills"
     "decrease the distance that you look ahead of your vehicle"]
    0]

   ["Which of these is not a good rule to follow when caring for injured at an accident scene?"
    ["If a qualified person is helping them, stay out of the way unless asked to assist"
     "Move severely injured persons if there is a danger due to fire or passing traffic"
     "Keep injured persons cool"]
    2]

   ["You are driving on a straight, level highway at 50 mph. There are no vehicles in front of you. Suddenly a tire blows out on your vehicle. What should you do first?"
    ["Stay off the brake until the vehicle has slowed down"
     "Begin light braking"
     "Begin emergency braking"]
    0]

   ["After starting the engine, the:"
    ["coolant temperature gauge should begin a gradual rise to normal"
     "oil pressure gauge should take 3-5 minutes to rise to normal"
     "engine temperature gauge w-ill take 3-5 minutes to rise to normal"]
    0]

   ["You are driving a heavy vehicle with a manual transmission. You have to stop the vehicle on the shoulder while driving on an uphill grade. Which of these is a good rule to follow when putting it back in motion?"
    ["Keep the clutch slipping while slowly accelerating"
     "Let the vehicle roll backwards a few feet before you engage the clutch, but turn the wheel so that the back moves away from the roadway"
     "Use the parking brake to hold the vehicle until the clutch engages"]
    2]

   ["Which of these statements about speed management is true?"
    ["Shady parts of the road allow better traction than open areas"
     "Road surfaces will freeze before bridges"
     "When the road is slippery, it will take longer to stop and it will be harder to turn without skidding"]
    2]

   ["When should you keep shipping papers when transporting hazardous materials?"
    ["Under the driver's seat"
     "In a pouch on the driver's door or on the seat"
     "In the glove compartment, which must be locked"]
    1]

   ["You should use your mirrors to check:"
    ["if your running lights are working properly"
     "blind spots only"
     "where the rear of your vehicle is while you make turns"]
    2]

   ["Extra care is needed to keep your vehicle centered in your lane because commercial vehicles:"
    ["are often wider than other vehicles"
     "tend to sway from side to side"
     "require a lot of room to change lanes"]
    0]

   ["Why is a broken exhaust system dangerous?"
    ["Poisonous fumes could enter the cab or sleeper berth"
     "You could pollute the air with exhaust smoke"
     "Oil on the tic rod may catch on fire"]
    0]

   ["Which of these statements about causes of vehicle fires is true?"
    ["Under-inflated tires will not cause a vehicle fire"
     "Carrying a properly charged fire extinguisher will help prevent fires"
     "Poor trailer ventilation can cause flammable cargo to catch on fire"]
    2]

   ["The purpose of retarders is to:"
    ["apply extra braking power I to the non-drive axles"
     "help prevent skids"
     "help slow the vehicle while driving and reduce brake wear"]
    2]

   ["Which of these is especially true about your tires on hot weather?"
    ["You should check tire mounting and air pressure before driving"
     "If a tire is too hot to touch, you should hose it down with water"
     "A small amount of air should be let out so air pressure remains steady"]
    0]

   ["Which of these is usually true about driving in tunnels?"
    ["Headlights are required by law"
     "There may be strong winds when exiting"
     "Speed are limited to 30 mph or less"]
    1]

   ["When driving to work zones, you should:"
    ["reduce speed only if workers are close to the roadway"
     "turn on your parking lights"
     "watch for sharp pavement drop-offs"]
    2]

   ["When you are passing another vehicle pedestrian, or bicyclist, you should assume that they:"
    ["may move into your traffic lane"
     "know you want to pass"
     "see your vehicle"]
    0]

   ["If you are not sure what to use to put out a hazardous materials fire, you should:"
    ["use water"
     "use dirt"
     "wait for a qualified firefighter"]
    2]

   ["What should you do when your vehicle hydroplanes?"
    ["Start stab braking"
     "Release the accelerator"
     "Accelerate slightly"]
    1]

   ["Which of these is true regarding the use of drugs while driving?"
    ["Prescription drugs are allowed if a doctor says the drugs will not affect safe driving ability"
     "No prescription or non prescription drugs are allowed any time, for any reason"
     "Use of amphetamines like 'speed' is allowed if you are using the drugs to stay awake"]
    0]

   ["Which of these statements about brakes is true?"
    ["The heavier a vehicle or the faster it is moving, the more heat the brakes have to absorb to stop it"
     "Brake drums cool very quickly when the vehicle is moving very fast"
     "Brake fade is not caused by heat"]
    0]

   ["You are driving a new truck with a manual transmission. What gear will you probably have to use to take a long, steep, downhill grade?"
    ["The same gear you would use to climb the hill"
     "lower gear than you would use to climb the hill"
     "higher gear than you would use to climb the hill"]
    1]

   ["Which of these statements about certain types of cargo is true?"
    ["Unstable loads such as hanging meat or livestock can require extra caution on curve"
     "Oversize loads can be transported without special permits during times when the roads are not busy"
     "When liquids are transported, the tank should always be loaded totally full"]
    0]

   ["Which of these statements about marking a stopped vehicle is true?"
    ["If a hill or curve blocks a driver's vision within 500 feet, move the rear"
     "The vehicle's taillights should be kept on to warn other drivers"
     "YOU Must Put out your emergency warning devices within 5 minutes"]
    0]

   ["Which of these is true about rear drive wheel braking skids?"
    ["Locked wheels usually have more traction than rolling wheels"
     "Front wheels slide sideways to try to 'catch up' with rear wheels"
     "On vehicles with trailers the trailer can push the towing vehicle sideways"]
    2]

   ["Which of these statements about double clutching and shifting is true?"
    ["Double clutching should not be used when the road is slippery"
     "Double clutching should only be used with a heavy load"
     "You can use the tachometer to tell you when to shift"]
    1]

   ["Which of these is true about your overhead clearance?"
    ["Paving a road does not affect the clearance height under bridges or overpasses"
     "The weight of a cargo can change the height of your vehicle"
     "Warnings are always posted for low clearance area like bridges and overpasses"]
    1]

   ["How far should a driver look ahead of the vehicle while driving?"
    ["5-8 seconds"
     "12-15 seconds"
     "18-21 seconds"]
    1]

   ["If you need to leave the road in a traffic emergency, you should:"
    ["try to get all wheels off the pavement"
     "avoid braking until your speed has dropped to about 20 mph"
     "avoid the shoulder because most shoulders will not support large vehicle"]
    1]

   ["Which of these statements about backing a heavy vehicle is not true?"
    ["You should back and turn toward the driver's side whenever possible"
     "You should use a helper and communicate with hand signals"
     "Because you can't see, you should back slowly until you slightly bump into the dock"]
    2]

   ["Which of these lights cannot be checked at the same time?"
    ["Headlights, brake lights, and clearance lights"
     "Turn signals, taillights, and clearance lights"
     "Turn signals, brake lights, and four-way flashers"]
    2]

   ["Which of these is correct about emergency or evasive action?"
    ["Stopping is always the safest thing to do in an emergency"
     "In order to turn quickly, you must have a firm grip on the steering wheel"
     "You can usually stop more quickly than you can turn to miss an obstacle"]
    1]

   ["You are driving a vehicle that could safely be driven at 55 mph on an open road. But traffic is now heavy, moving at 35 mph, in a 55-mph zone. The safest speed for your vehicle is most likely:"
    ["25 mph"
     "45 mph"
     "35 mph"]
    2]

   ["According to the Commercial Driver Supplement, why should you limit the use of your horn?"
    ["It can startle other drivers"
     "On vehicles with air brakes, it can use air pressure that may be needed to stop"
     "You should keep both hands tightly gripping the steering wheel at all times"]
    0]

   ["Which of these is true about bad weather and driving conditions?"
    ["When the temperature drops, bridges will freeze before roads"
     "The road is more slippery as rain continues than when rain begins"
     "Driving conditions become more dangerous as the temperature rises above freezing"]
    0]

   ["To determine blood alcohol concentration (BAC) for a person, it is necessary to know:"
    ["how often the person drinks alcohol (tolerance level)"
     "how much the person weighs"
     "how old the person is"]
    1]

   ["Which of these happens when a tire blows out at highway speed?"
    ["vibrating feeling"
     "hissing sound"
     "rapid drop in speed to less than 20 mph"]
    0]

   ["You are driving a 40-foot vehicle at 35 mph. The road is dry and visibility is good.  What is the least amount of space that you should keep in front of your vehicle to be safe?"
    ["3 seconds"
     "4 seconds"
     "5 seconds"]
    1]

   ["Truck escape ramps:"
    ["cannot be used by certain types of heavy vehicles"
     "are not for buses"
     "help avoid damage to vehicles"]
    2]

   ["When hydraulic brakes fail while driving, the system won't build up pressure and brake pedal will feel spongy or go to the floor. What action should you take?"
    ["Push down on the brake pedal as hard as you can"
     "Pump the brake pedal to generate pressure"
     "Put the vehicle in neutral and set the parking brake"]
    1]

   ["Which of these items is not checked in a pre-trip inspection?"
    ["Whether all vehicle lights are working and are clean"
     "Cargo securement"
     "Amount of fuel in the vehicle"]
    2]

   ["Which of these systems should receive extra attention during a winter weather inspection?"
    ["Exhaust"
     "Suspension"
     "Steering"]
    0]

   ["The total weight of powered unit, the trailer, and the cargo is called:"
    ["Gross vehicle weight"
     "Gross combination weight"
     "Gross axle weight"]
    1]

   ["You are driving a long vehicle that makes wide turns. You want to turn left from one street into another. Both are two-lane, two- way streets. You should:"
    ["begin turning your vehicle as soon as you enter the intersection"
     "turn into the left lane and then move to the right lane when the traffic is clear"
     "begin turning your vehicle when you are halfway through the intersection"]
    2]

   ["Which statement is true?"
    ["Most people are more alert at night than during the day"
     "Most hazards are easier to see at night than during the day"
     "Many heavy vehicle accidents occur between midnight and 6 a.m."]
    2]

   ["Controlled braking:"
    ["involves locking the wheels for short periods of time"
     "is used to keep a vehicle in a straight line when braking"
     "should only be used with hydraulic brakes"]
    1]

   ["When a coolant container is part of a pressurized system, which of these is true?"
    ["The radiator cap can be safely removed and coolant added while the engine is hot"
     "You never need to check the antifreeze in such a system"
     "You can check the coolant level of a hot engine"]
    2]

   ["Which of these is the good thing to do when driving at night?"
    ["Keep your speed slow enough that you can stop within the range of your headlights"
     "Look directly at oncoming headlights only briefly"
     "Keep your instrument lights bright"]
    1]

   ["You should avoid driving through deep paddles or flowing water.  But if your must, which of these steps can help keep your brakes working?"
    ["Applying hard pressure on both e the brake pedal and accelerator after coming out of the water"
     "Gently putting on the brakes while driving through water"
     "Turning on your brake heaters"]
    1]

   ["Stab braking:"
    ["involves applying brakes and releasing them after the wheels lock up"
     "should be used on vehicle without antilock brake system"
     "should only be used with hydraulic brakes"]
    1]

   ["'Implied consent' means:"
    ["it is understood that you may drink- alcohol now and then"
     "you have given your consent to be tested for alcohol in your blood"
     "you have given your consent to inspection of your vehicle for alcohol"]
    1]

   ["Which of these should be tested while the vehicle is stopped?"
    ["Service brake"
     "Parking brake"
     "Hydraulic brake"]
    2]

   ["You must complete a written vehicle inspection report each day and you must sign:"
    ["the previous driver's report when any defects are noted"
     "only when defects are noted that are certified to be repaired"
     "only when defects are noted that are certified to not need repair"]
    0]

   ["Which of the below is NOT characteristics of a front tire failure?"
    ["Steering wheel twisting"
     "Vehicle fishtail"
     "Difficult steering"]
    1]

   ["What is the proper way to hold a steering wheel?"
    ["With both hands closed together, near the top of the wheel"
     "With both hands closed together, near the bottom of the wheel"
     "With both hands, on opposite sides of the wheel"]
    2]

   ["Which of these is true about mirror adjustment?"
    ["You should adjust your mirror prior to starting a trip"
     "The mirrors can be adjusted correctly even if the trailer is not straight"
     "You can adjust the mirrors even while you are driving"]
    0]

   ["What is counter steering?"
    ["Turning the steering wheel counterclockwise"
     "Using the steering axle brakes to prevent oversteering"
     "Turning the wheel back in the other direction after steering to avoid a traffic emergency"]
    2]

   ["The distance that you should look ahead of your vehicle while driving amounts to about mile at highway speed."
    ["1/8"
     "11/4"
     "1/2"]
    1]

   ["Why will your vehicle's speed naturally increase on downgrades?"
    ["The engine runs smoother"
     "Gravity"
     "Lack of traction"]
    1]

   ["Which of these pieces of emergency equipment should be carried in your vehicle?"
    ["Circuit breakers"
     "First Aid Kit"
     "Warning devices for parked vehicles"]
    2]

   ["During a vehicle inspection, checking the ---- will NOT prevent a fire."
    ["cargo ventilation"
     "electrical system insulation"
     "battery fluid level"]
    2]

   ["You should signal continuously while turning because:"
    ["you need both hands on the wheel to turn safely"
     "it is illegal to turn off your signal before completing a turn"
     "most vehicles have self-canceling signals"]
    0]

   ["Why is a broken exhaust system dangerous'?"
    ["Poison fumes could enter the cab or sleepers berth"
     "You could pollute the air with exhaust smoke"
     "Loud noise could damage the driver's ears"]
    0]

   ["You can see a marking on a vehicle ahead of you. The marking is a red triangle with with an orange center. What does the marking mean?"
    ["It may be a slow moving vehicle"
     "The vehicle is hauling hazardous materials"
     "It is being driven by a student driver"]
    0]

   ["Most serious skids result from;"
    ["turning sharply"
     "decelerating too fast"
     "driving too fast for condition"]
    2]

   ["When you are parked at the side of the road at night, you must:"
    ["put out your emergency warning devices within 30 minutes"
     "use your taillights to give warning to other drivers"
     "turn on your 4-way emergency flashers to warn others"]
    2]

   ["You do NOT have a HAZMAT endorsement on your commercial driver license. You can drive a vehicle hauling hazardous materials when:"
    ["the GVWR is 26,001 pounds of less"
     "the vehicle does not require placards"
     "a person who has the HAZMAT endorsement rides with you"]
    1]

   ["To avoid a crash, you have to drive onto the right shoulder. You are now driving at 40 mph on the shoulder. How should you move back onto the pavement?"
    ["If the shoulder is clear, stay on it until your vehicle has come to a stop. Then move back onto the pavement when it is safe."
     "Brake hard to slow the vehicle, then steer sharply onto the pavement"
     "Keep moving at the present speed and steer very gently back onto the pavement"]
    0]

   ["Containerized loads:"
    ["should come with their own tiedown devices or locks"
     "do not require inspection or securing by the driver"
     "are generally used when freight is carried part way by rail or ship from t-lie rear. The next time 	you check your your"]
    2]

   ["in your mirror you see a car approaching from the rear. The next time you check your mirror you do not see the car. You wish to change lanes. You should:"
    ["wait to change lanes until you are sure the car isn't in your blind spot"
     "ease into the other lane slowly so the car can get out of the way if it is beside you"
     "assume the car left the road and change lanes as normal"]
    0]

   ["Whenever you double your speed, your vehicle has about --- times the destructive power if it crashes"
    ["two"
     "three"
     "four"]
    2]

   ["You are starling your vehicle in motion from a stop. As you apply power to the drive wheels, they start to spin. You should:"
    ["take your loot off the accelerator"
     "take your foot off the accelerator and apply the brakes"
     "try lower gear"]
    0]

   ["Placards must be:"
    ["displayed on all vehicles hauling any amount of hazardous materials"
     "placed only on the front and rear of a vehicle"
     "placed on all four sides of a vehicle"]
    2]

   ["You are driving a new truck with a manual transmission. What gear will you probably have to use to take a long, steep downhill grade?"
    ["The same gear you would use to climb the hill"
     "higher gear than you would use to climb the hill"
     "lower gear than you would use to climb the hill"]
    2]

   ["A car suddenly cuts in front of you, creating a hazard. Which of these actions should you NOT take?"
    ["Honk and stay close behind the car"
     "Slow down to prevent a crash"
     "Signal and change lanes to avoid it , if possible"]
    0]

   ["What should you do if you are unsure whether you have enough overhead clearance?"
    ["Slow down slightly and try to drive under the object"
     "Find another route that will not require driving under the object"
     "Estimate the height of an overhead object it is not posted"]
    1]

   ["You are driving a heavy vehicle. You must exit a highway using an offramp that curves downhill. You should:"
    ["slow down to a safe speed before the curve"
     "slow to the posted speed limit for the offramp"
     "wait until you are in the curve before the downshifting"]
    0]

   ["You are driving a vehicle at 55 mph on dry pavement. About how much total stopping distance will you need to bring it to a stop?"
    ["Twice the length of the vehicle"
     "Half the length of the football field"
     "The length of a football field"]
    2]

   ["Which of these is NOT part of the pre-trip inspection of the engine compartment?"
    ["engine level"
     "valve clearance"
     "Worn electrical wiring insulation"]
    1]

   ["On which fires can you use water?"
    ["Electrical"
     "Gasoline"
     "Tires"]
    2]

   ["What is the term for a commercial vehicle's habit of swinging wide on turns?"
    ["Offtracking"
     "Wide-rounding"
     "Sidewinding"]
    0]

   ["Backing a commercial vehicle is:"
    ["not dangerous if you have a helper"
     "not dangerous if you do not have to turn"
     "always dangerous"]
    2]

   ["Convex (curved) mirrors:"
    ["make objects appear larger than they really are"
     "make objects appear closer than they really are"
     "show a wider area than flat mirrors show"]
    2]

   ["Your brakes can get wet when you drive through a heavy rain.  What can this cause when the brakes are applied"
    ["Hydroplaning"
     "Excessive brake wear"
     "Trailer jackknife"]
    2]

   ["If you are being tailgate, you should:"
    ["flash your brake lights"
     "signal the tailgater when it is safe to pass you"
     "increase your following distance"]
    2]

   ["When driving in cold weather, your tire thread should:"
    ["be check every 100 miles or every two hours"
     "be double the depth required in normal weather"
     "provide enough traction to steer and push the vehicle through snow"]
    1]

   ["Which of these statements about staying alert to drive is true?"
    ["A half-hour break for coffee will do more to keep you alert than a half-hour nap"
     "If you must stop, pull over on the side of the road for a short nap"
     "Sleep is the only thing that can overcome fatigue"]
    2]

   ["if you are checking your brakes and suspension system for a pre-trip inspection.  Which of these statements is true?"
    ["just one missing leaf spring is not dangerous"
     "Spring hangers that cracked but still tight is not dangerous"
     "Brake shoes should not have oil, grease, or brake fluid on them"]
    2]

   ["For a first offense of driving a commercial vehicle under the influence of alcohol or drugs, you will lose your CDL for at least -----"
    ["6 months"
     "2 years"
     "I year"]
    2]

   ["A full stop is required at a railroad grade when:"
    ["the crossing is located in a city or town with frequent train traffic"
     "there are no flagmen, warning signals, or gates at the crossing"
     "the nature of the cargo makes a stop mandatory under state or federal regulations"]
    2]

   ["If you must cross into the oncoming lane as you make a turn, you should:"
    ["allow enough space to get completely across the intersection"
     "back up to allow oncoming traffic to pass"
     "complete your turn without stopping"]
    0]

   ["The best drivers are those who watch and prepare for hazards.  This is called being:"
    ["offensive"
     "defensive"
     "objective"]
    1]

   ["If you do not have a CB radio, what is the first thing that you should do at an accident scene?"
    ["Protect the area"
     "notify authorities"
     "Care for the injured"]
    0]

   ["The Commercial Driver Supplement suggests several things to do when you pass a vehicle.  Which of these is NOT one of them?"
    ["Lightly tap horn when needed"
     "At night, turn on your high beam before you start to pass and leave then on until have completely passed the vehicle"
     "Assume the driver does not see you"]
    1]

   ["One reason that dry bulk tanks require special care is:"
    ["they can only be filled halfway"
     "they can easily catch fire"
     "the load can shift"]
    2]

   ["You are driving a 40 foot vehicle at 35 mph. The road is dry and visibility is good. What is the least amount of space that you should keep in front of your vehicle to be safe?"
    ["3 seconds"
     "4 seconds"
     "5 seconds"]
    1]

   ["Whenever you double your speed, it takes about --- times as much distance to stop:"
    ["two"
     "three"
     "four"]
    2]

   ["Optional safety equipment may include emergency phone numbers, tire chains, and :"
    ["tire changing equipment"
     "red reflective triangles"
     "charged fire extinguisher"]
    0]

   ["'Perception distance' is the distance your vehicle travels from the time:"
    ["the eyes see the hazard to the time the foot pushes the brake pedal"
     "the brain tells the foot to push the brake pedal to the time the foot responds"
     "the eyes see a hazard to the time the brain knows it is a hazard"]
    2]

   ["If you have a heavy load that is slowing you down on an upgrade, you should:"
    ["exit the road until traffic is lighter"
     "drive on the shoulder so that others can pass easily"
     "shift into a lower gear"]
    2]

   ["Which of these may be a sign of tire failure?"
    ["A loud hissing noise"
     "Gentle thumping"
     "Wheels fishtailing"]
    2]

   ["Strong winds most affect driving:"
    ["on open highways"
     "on bridges or overpasses"
     "upon exiting tunnels"]
    2]

   ["You are checking your tires for a pre-trip inspection. Which of these statements is true?"
    ["Tires of mismatched sizes should not be used on the same vehicle"
     "Radial and bias-ply tires can be used together on the same vehicle"
     "2/32 inch thread depth is safe for the front tires"]
    0]

   ["What should you do if a car coming toward you at night keeps its high beams on?"
    ["Flash your high beams quickly at the other driver"
     "Look to the right lane or edge markings of the road"
     "Slow down and look straight ahead in your lane"]
    1]

   ["To avoid a crash, you had to drive onto the right shoulder. You are now driving at 40 mph on the shoulder. How should you move back onto the pavement?"
    ["Brake hard to slow the vehicle, then steer sharply onto the pavement"
     "Keep moving at the present speed and steer very gently back on the pavement"
     "If the shoulder is clear, stay on it until the vehicle has come to a stop.  Then move back onto the pavement when it is safe."]
    2]

   ["How many missing or broken leaves in any leaf spring will cause a commercial vehicle to be placed out of service?"
    ["One-fourth of the total number"
     "One-third of the total number"
     "One-half of the total number"]
    0]

   ["What will keep an engine cool in hot weather?"
    ["Driving faster to force more air into the radiator"
     "Running the air conditioner"
     "Making sure that the engine has the right amount of oil"]
    2]

   ["You can see a marking on a vehicle ahead of you. The marking is a red triangle with an orange center. What does the marking mean?"
    ["It may be a slow-moving vehicle"
     "The vehicle is hauling hazardous materials"
     "It is being driven by a student driver"]
    0]

   ["Which of these is NOT a danger of rough acceleration?"
    ["Tire damage"
     "Mechanical damage"
     "Damage to coupling"]
    0]
   
   ["Which of these is a good thing to do when steering to avoid a crash?"
    ["Don't turn any more than needed to clear what is in your way"
     "Avoid countersteering"
     "Apply the brake while turning"]
    0]

   ["In mountain driving, you will have to use lower gears to drive safely on the grades.  Which of these does NOT affect you choice of gears?"
    ["Weight of the load"
     "Tire thread type"
     "Length of the grade"]
    1]

   ["Which of these statements about backing a heavy vehicle is NOT true?"
    ["You should back and turn toward the driver's side whenever possible"
     "You should use a helper and communicate with hand signals"
     "Because you can't see, you should back slowly until you slightly bump into the dock"]
    2]

   ["How far should the driver look ahead of the vehicle while driving?"
    ["1-2 seconds"
     "7-11 seconds"
     "12-15 seconds"]
    2]

   ["Which of these is NOT a type of retarder?"
    ["Electric"
     "Hydraulic"
     "Robotic"]
    2]

   ["Which of these is NOT true about engine belts in hot weather?"
    ["Cracking is likely to occur but is not safety threat"
     "You can check the tightness of the belts by pressing on them"
     "Loose belts will not turn the water pump and/ or fan on properly"]
    0]

   ["Which of these is NOT true?"
    ["Alcohol goes directly from the stomach to the blood stream"
     "A drinker can control how fast his or her body absorbs and gets rid of alcohol"
     "BAC is determine by how fast you drink-, how much you drink and your weight"]
    1]

   ["You should try to park so that:"
    ["you can pull forward when you leave"
     "there is at least one curb next to your vehicle"
     "your vehicle is protected by trees or some other overhang"]
    0]

   ["The engine braking effect is greatest when the engine is --- the governed RPMs and the transmissions is in the ----- gears."
    ["above, lower"
     "below, higher"
     "near, lower"]
    2]

   ["Which of these is NOT a proper use of vehicle lights?"
    ["Turning on your headlights during the day when visibility is reduced due to rain or snow"
     "Flashing your brake lights to warn someone behind you that you are slowing down"
     "Flashing your brake lights to get someone off your tail"]
    2]

   ["What hat should you do before driving in mountains?"
    ["Know the escape ramp locations on your route"
     "Unhook your steering axle brakes"
     "Carry tire chains in your vehicle"]
    0]

   ["Which of these statements about shipping hazardous materials is true?"
    ["A four-inch, circular, red hazardous materials level must be on the container"
     "Gas cylinders that will not hold a label must be shipped under cover"
     "A four-inch, diamond-shaped hazardous materials level must be on the container"]
    2]

   ["If a straight vehicle (no trailer or articulation) goes into a front-wheel skid, it will:"
    ["slide sideways and spin out"
     "go straight ahead even if the steering wheel is turned"
     "go o straight ahead but will turn if you turn the steering wheel,"]
    1]

   ["Retarders:"
    ["allow you to disconnect the steering axle brakes"
     "cannot be used on interstate highways"
     "can cause the drive wheels to skid when they have poor traction"]
    2]

   ["Which of these is the proper way to signal to change lanes?"
    ["Signal just as you begin to change lanes, and drift over slowly"
     "Signal early before you turn, and change lanes slowly and smoothly"
     "Signal before you change lanes, and then move over quickly"]
    1]

   ["When stopped on a on-way divided highway, you should place reflective triangles at:"
    ["10 feet, 100 feet, and 500 feet toward approaching traffic"
     "10 feet, 100 feet and 200 feet toward approaching traffic"
     "10 feet, 50 feet and 100 feet toward approaching traffic"]
    1]

   ["For your safety, when setting out reflective triangles you should:"
    ["Carry the triangles at your side"
     "Hold the triangles with the reflective side between yourself and oncoming traffic"
     "Keep them out of sight while you walk to the spots where you set them out"]
    1]

   ["What is the first thing you should do if your brakes fail while going down a hill?"
    ["Call or radio for help"
     "Try to use your parking brakes to stop"
     "Get off the road as soon as possible"]
    2]

   ["The distance that you should look ahead of your vehicle while driving amounts to about---- mile at highway speed."
    ["1/8"
     "1/4"
     "1/2"]
    1]

   ["What is the best advice for drivers when a heavy fog occurs?"
    ["Do not drive too slowly, or other drivers may hit you"
     "Alternate your low and high beam to improve your vision"
     "Park at a rest area or truck stop until the fog has lifted"]
    2]

   ["Which of these is true about hazardous materials?"
    ["All hazardous materials present a health and safety threat"
     "Every truck carrying any amount of hazardous materials have placards"
     "All public roads allow trucks carrying hazardous materials if they are loaded correctly"]
    0]

   ["How do you correct a rear wheel breaking skid on ice or show?"
    ["Apply more power to the wheels"
     "Stop accelerating"
     "Downshifting"]
    1]

   ["You are checking your brakes and suspension system for a pre-trip inspection.  Which of these Statements is true?"
    ["just one missing leaf spring is not dangerous"
     "Spring hangers that are cracked but still light are not dangerous"
     "Brake shoes should not have oil, grease, or brake fluid on them"]
    2]

   ["Which of these is a good thing to remember about drinking alcohol?"
    ["The driver can control how fast the body gets rid of alcohol"
     "Small quantities of alcohol improve reaction time"
     "Alcohol first affects judgment and self control, which are necessary for safe driving"]
    2]

   ["When starting to move up a hill from a stop:"
    ["Partly engage the clutch and then release the parking brake"
     "Engage the clutch and accelerate quickly"
     "Keep the trailer brake hand valve applied until you reach twenty mph"]
    0]])

(def passenger-db
  [["If you have riders on the bus, you should never fuel your bus:"
   ["Without a fire extinguisher beside you"
    "In a closed building"
    "With any of the windows open"]
   1]

  ["Baggage or freight carried on a bus must be secured:"
   ["In a separate compartment away from passengers"
    "In front of a standee line"
    "So any door or window can be used in an emergency"]
   2]

  ["You are driving at night and you must dim your headlights from high to low.  What should you do with your speed?"
   ["Drop 5 mph until your eyes adjust"
    "Nothing. How well you can see should not affect speed"
    "Slow down"]
   2]

  ["Which of these actions will result in the best control on a curve?"
   ["Slow to a safe speed before entering a curve, then accelerate slightly through it"
    "Maintain constant speed through a curve, allowing the bus to lean slightly"
    "Slow to a safe speed before entering a curve, then coast through it"]
   0]

  ["When waiting to merge into traffic you should:"
   ["Always wait for a large enough gap before merging"
    "Pull out slowly and gradually reach the speed of traffic"
    "Drive on the shoulder of the road until you reach the speed of traffic"]
   0]

  ["Which of these statements is true about managing space to the sides of your bus?"
   ["You should keep your but to the right side of your lane"
    "You should avoid traveling next to others when possible"
    "You should keep your bus to the left side of your lane"]
   1]

  ["Drivers of charter buses should not allow riders on the bus until:"
   ["All baggage has been loaded"
    "All scheduled passengers have arrived at the station"
    "Departure time"]
   2]

  ["How many folding seats are allowed in a bus that is not carrying farm workers?"
   ["0"
    "4"
    "8"]
   0]

  ["When you discharge an unruly rider, you should choose a place:"
   ["Near a police station"
    "That is as safe as possible"
    "Convenient for you"]
   1]

  ["If your bus has an emergency exit door, it must:"
   ["Be closed when operating the bus"
    "Always have a red door light and it must be lighted"
    "Sound an alarm when opened"]
   0]

  ["You may sometimes transports small arms ammunition or emergency hospital supplies on a bus. The total weight of these hazardous materials must not exceed pounds."
   ["5"
    "50"
    "500"]
   2]

  ["You are driving a 40 foot bus at a 30 mph. The road is dry and visibility is good.  You should keep a safety space in front of your bus that is at least ----- seconds."
   ["8"
    "4"
    "3"]
   1]

  ["A hazard is defined as:"
   ["Another driver only"
    "A condition of the roadway or weather only"
    "Any condition which may cause your trip to be unsafe"]
   2]

  ["Which of the following types of emergency equipment must you have on your bus"
   ["Reflectors, fire extinguisher, tire repair kit"
    "First aid kit, spare electric fuses, fire extinguisher"
    "Fire extinguisher, spare electric fuses, reflectors"]
   2]

  ["If a rider wants to get on the bus with a car battery or a can of gasoline, you should:"
   ["Not allow the rider to get on"
    "Tell the rider to sit on the rear of the bus"
    "Put the battery or gasoline in the cargo compartment"]
   0]

  ["Carry-on baggage:"
   ["Always needs to be stored in the overhead compartments or under the seats"
    "Can never be left in a doorway or an aisle"
    "Should always be inspected by the driver as it is carried onto the bus"]
   1]

  ["Buses may have recapped or regrooved tires:"
   ["Only as the outside tire on a set of duals"
    "On any or all of the wheels"
    "Anywhere except the front wheels"]
   2]

  ["You are driving a 40 foot bus at 50 mph. The road is dry and visibility is good. You should keep at least ---- seconds of space in front of your bus to be safe."
   ["5"
    "6"
    "8"]
   0]

  ["The maximum distance you should stop away from a railroad crossing is:"
   ["20 feet"
    "50 feet"
    "70 feet"]
   1]

  ["When driving, a hazard is:"
   ["caused by condition you cannot control"
    "less important than an emergency"
    "anyone or anything that may cause an unsafe condition"]
   2]

  ["You should not let riders stand:"
   ["between the wheel wells"
    "within two feet of an emergency exits"
    "in front of the standee line"]
   2]

  ["If a bus breaks down and there are passengers aboard, the bus:"
   ["may be towed to the nearest service garage"
    "should not be towed until the passengers are discharged"
    "may be towed to the nearest safe spot to discharge passengers"]
   2]

  ["Which of the following lists the three types of emergency equipment that you must have on your bus?"
   ["Reflectors, fire extinguisher, tire repair kit"
    "Fire extinguisher, spare electric fuses unless equipped with circuit breakers, reflectors"
    "First aid kit, spare electric fuses unless equipped with circuit breakers, fire extinguisher"]
   1]

  ["While driving the bus, you should:"
   ["scan the interior of your bus, as well as the road ahead, to the sides and to the rear"
    "focus your attention on traffic immediately to the side of your bus"
    "scan the road 5 - 7 seconds ahead of you"]
   0]

  ["Which of the following types of cargo can never be carried on a bus?"
   ["Small arm ammunition labeled ORM-D"
    "Irritating materials or tear gas"
    "Labeled radioactive materials in the passenger area"]
   1]

  ["If a bus leans in a curve, you:"
   ["should increase your speed slightly"
    "should quickly press hard on the brake"
    "are driving too fast"]
   2]

  ["When you inspect your bus make sure that:"
   ["rider signaling devices are working"
    "emergency exit handles have been removed"
    "closed access panels are open"]
   0]

  ["When driving a bus, you should look ---- seconds ahead."
   ["3-6"
    "7-10"
    "12-15"]
   2]

  ["The total maximum weight of hazardous materials that buses are allowed to carry is ------ pounds."
   ["100"
    "250"
    "500"]
   2]

  ["The posted speed for turns:"
   ["may be too slow for a bus"
    "may be unsafe for a bus"
    "is the safe speed for a bus"]
   1]

  ["When you discharge an unruly rider, you should use a place that is:"
   ["near a police station"
    "convenient for you"
    "as safe as possible"]
   2]

  ["Which of these statements about speed management and braking is true?"
   ["Stopping time increases one second for each 10 mph over 20 mph"
    "You need about four times as much stopping distance at 40 mph as at 20 mph"
    "The total stopping distance of a bus is the distance it takes to stop once the brakes are put on"]
   2]

  ["You are driving on a slippery road during the day. How much space does the Commercial Driver Supplement say you should keep ahead of you?"
   ["Add 'one second to the space needed in good road conditions'"
    "Allow 'one car length for every 10 mph'"
    "Allow 'much more space' than needed for ideal driving conditions"]
   2]
  
  ["Buses are not allowed to carry more than ----- pounds of any one class of allowed hazardous materials."
   ["100"
    "250"
    "500"]
   0]
  
  ["A bus must stop at least ----- feet before the draw of a drawbridge that does not have a signal light or an attendant."
   ["10"
    "30"
    "50"]
   2]

  ["When driving a bus, the emergency roof hatches:"
   ["must always be completely closed and locked while the bus is moving"
    "may be locked in a partially open position for fresh air"
    "may be left in a closed but unlocked position"]
   1]

  ["You are driving a 30 foot bus on a highway. The road is dry and visibility is good.  The distance between you and the vehicle ahead of you should be not less than:"
   ["3 seconds"
    "4 seconds"
    "5 seconds"]
   1]

  ["When is it OK to drive with an emergency exit door open?"
   ["It is never OK to drive with an emergency exit door open"
    "When you will only be moving a short distance"
    "If the temperature in the cabin is uncomfortably hot"]
   0]

  ["To stop for railroad tracks, a bus driver should stop ---- to ---- feet before the nearest track."
   ["5:20"
    "10:35"
    "15:50"]
   2]

  ["Your bus is disabled. A bus with riders aboard, may be towed or pushed to a safe spot to discharge the passengers only:"
   ["if the distance is less than 1 mile"
    "if getting off the bus sooner would be unsafe"]
   1]

  ["If you work for an interstate carrier, you must complete an after-trip inspection report that specifies for each bus:"
   ["any defects that would affect the safety or result in a breakdown"
    "fuel usage, distance traveled, and mileage for that trip"
    "any problems that occurred with riders"]
   0]

  ["Carrying hazardous materials on a bus is:"
   ["permitted if the material's container is placarded"
    "permitted if they meet certain conditions"
    "never permitted"]
   1]

  ["Many buses have curved (convex or 'spot') mirrors. These mirrors:"
   ["are against the law in some states"
    "make things smaller and farther away than they really are"
    "do not need to be checked often because they show a larger area"]
   1]

  ["You must sign the inspection report made by the previous driver only:"
   ["when some of the repairs have not been made"
    "if the driver who filled out the report is present"
    "if the defects reported have been certified as repaired or not needing repair"]
   2]

  ["A bus may carry baggage and freight only if it is:"
   ["stored in a separate baggage or freight compartment"
    "owned by a riding passenger"
    "secured and out of the way of any exit"]
   2]

  ["You are driving at night and you must dim your headlights from dim to low. What should you do with your speed?"
   ["Drop 5 mph until your eyes adjust"
    "Slow down"
    "Nothing. How well you can see should not affect your speed"]
   1]

  ["While driving a bus, a bus driver:"
   ["can only talk with riders if cruising on a highway"
    "should only allow riders to talk to you if they can stay in their seat"
    "should never talk unnecessarily with riders"]
   2]

  ["Which of these statements about seeing ahead is true?"
   ["Good drivers shift their attention back and forth, near and far"
    "Good drivers keep their attention on one place for 12-15 seconds at a time"
    "At highway speeds, you should look 3-5 seconds ahead of you"]
   0]

  ["When driving a bus across railroad tracks you:"
   ["do not have to stop, but must slow down at tracks where the crossing gates are in the upright position"
    "may sometimes need to stop more than 75 feet from the tracks"
    "should never change gears if bus has a manual transmission"]
   2]

  ["Which of these will result on the best control in curves?"
   ["Brake all the way through curves"
    "Slow to a safe speed before entering curves, then accelerate slightly through the curves"
    "Slow to a safe speed before entering curves, then coast through them"]
   1]

  ["Buses are allowed to carry less than 100 pounds of --- in the passenger area"
   ["small arms ammunition (ORM-D)"
    "labeled radio active materials"
    "liquid Class 6 poisons"]
   0]

  ["When you arrive at a bus stop, you should announce the:"
   ["reason for stopping, next departure time, estimated time of arrival,(ETA) at next destination, and the bus number"
    "location, reason for stopping, ETA of arrival for next destination, and the bus number"
    "location, reason for stopping, next departure time, and the bus number"]
   2]

  ["If there is no traffic light or attendant, stop for a drawbridge about --- feet from the draw"
   ["25"
    "50"
    "100"]
   1]

  ["You are driving a 40 foot bus at 30mph. The road is dry and visibility is good.  You should keep a safety spot in front of your bus that is at least ---- seconds."
   ["8"
    "4"
    "3"]
   1]

  ["A standee line on a bus:"
   ["is required when carrying less than 15 riders"
    "indicates where you should stand when you are talking to the passengers"
    "is the line that all riders must stay behind while the bus is moving"]
   2]

  ["Your bus is disabled. The bus, with riders aboard, may be towed or pushed to a safe spot to discharge the passengers only if:"
   ["the distance is less than 1 mile"
    "a peace officer of rescue crew is present"
    "getting off the bus sooner would be unsafe"]
   2]

  ["If you have riders aboard, you should never fuel your bus:"
   ["without a fire extinguisher beside you"
    "in a closed building"
    "with any of the windows open"]
   1]

  ["If a rider wants to bring a car battery or a can of gasoline aboard your bus, you should:"
   ["not allow the rider to do it"
    "tell the rider to sit in the rear of the bus"
    "put the battery or gasoline in the cargo compartment"]
   0]

  ["You should check your mirrors:"
   ["right after starting a lane change"
    "as you are preparing to stop"
    "regularly as part of your scan for potential hazards"]
   2]

  ["You are driving at night and you must dim your headlights from high to low. What should you do with your speed?"
   ["Drop 5 mph until your eyes adjust"
    "Nothing. How well you can see should not affect speed"
    "Slow down"]
   2]

  ["Carry on baggage:"
   ["always needs to be stored in the overhead compartments or under the seats"
    "can never be left in a doorway or an aisle"
    "should always be inspected by the driver as it is carried on to the bus"]
   1]

  ["Which of these statements about managing space to the sides of your bus is true?"
   ["You should keep your bus to the right side of your lane"
    "You should avoid traveling next to others when possible"
    "You should keep your bus to the left side of your lane"]
   1]

  ["When you discharge an unruly rider, you should choose a place that is:"
   ["near a police station"
    "as safe as possible"
    "convenient for you"]
   1]

  ["When waiting to pull out into traffic, you should:"
   ["always wait for the proper gap"
    "put out slowly"
    "drive on the shoulder until you reach the speed of traffic"]
   0]

  ["Which of these will result in the best control on curves?"
   ["Slow to a safe speed before entering curves, then accelerate slightly through them"
    "Maintain constant speed through curves, keeping the bus leaning slightly"
    "Slow to a safe speed before entering curves, then coast through them"]
   0]

  ["You may sometimes transport small arms ammunition or emergency hospital supplies on on a bus. The total weight of all such hazardous material must not be more than ---- pounds"
   ["5"
    "50"
    "500"]
   2]

  ["Charter bus drivers should not allow riders on the bus until:"
   ["all baggage has been loaded"
    "all scheduled passengers have arrived at the station"
    "departure time"]
   2]])

(def safety-db
  [["If you have an accident, the law requires you to exchange your driver license information with:"
    ["Fire fighters"
     "Others involved in the accident"
     "Security guards"]
    1]
   
   ["When traffic is slow and heavy and you must cross railroad tracks before reaching the upcoming intersection, you should:"
    ["Stop between the crossing gates in case they close"
     "Stop on the tracks and wait for your light to turn green"
     "Wait until you completely cross the tracks before proceeding"]
    2]

   ["To make a right turn onto a two-way street, start in the right-hand lane and end in:"
    ["The left lane"
     "The lane closest to the curb"
     "Any lane that is available"]
    1]

   ["You are at a red traffic signal. The traffic light turns green, but there are still other vehicles in the intersection. You should:"
    ["Wait until the vehicles clear the intersection before entering"
     "Move ahead only if you can go around the other vehicle safely"
     "Enter the intersection and wait for traffic to clear"]
    0]

   ["U-turns in residential districts are legal:"
    ["On a one-way street at a green arrow"
     "When there are no vehicles approaching nearby"
     "Across two sets of solid double, yellow lines"]
    1]

   ["Always look carefully for motorcycles before changing lanes because:"
    ["Their smaller size makes them harder to see"
     "They usually have the right-of-way at intersection"
     "It is illegal for motorcycles to share traffic lanes"]
    0]

   ["It is illegal for a person 21 years of age or older to drive with a blood alcohol concentration (BAC) that is ----- or higher"
    ["0.10 %- One tenth of one percent"
     "0.08 %- Eight hundredths of one percent"
     "0.05 %- Five hundredths of one percent"]
    1]

   ["You are driving 45 mph in a 55 mph zone. You could be given a citation for speeding:"
    ["Only if you are approaching a sharp curb on the road"
     "Under no circumstances because it is always legal"
     "If road or weather conditions require an even slower speed"]
    2]

   ["If there is a solid double, yellow line in the center of the roadway, you:"
    ["May turn left into a private driveway"
     "Are on a one-way street"
     "Are in a center left-turn lane"]
    0]

   ["When driving at night on a dimly lit street, you should:"
    ["Drive slowly enough so you can stop within the area lighter by your headlights"
     "Look directly at oncoming headlights for a short time"
     "Keep instrument lights bright to be more visible to other drivers"]
    0]

   ["A green arrow means go, but first you must:"
    ["Yield to any vehicle, bicycle, or pedestrian in the intersection"
     "Check the vehicle behind you in your rear view mirror"
     "Wait five seconds before proceeding"]
    0]

   ["You should dim your lights for oncoming vehicles or when you are within 300 feet of a vehicle:"
    ["You are approaching from behind"
     "Approaching you from behind"
     "You have already passed"]
    0]

   ["You are going to make a left turn from a dedicated left-turn lane when a yellow arrow appears to your lane. You should:"
    ["Speed up to get through the intersection"
     "Stop and not turn under any circumstances"
     "Be prepared to obey the next signal that appears"]
    2]

   ["When you enter traffic from a stop (ex. pulling away from the curb), You:"
    ["Should drive slowly than other traffic for 200 feet"
     "Need a large enough gap to get up to the speed of traffic"
     "Should wait for the first vehicle to pass, then pull into the lane"]
    1]

   ["You must notify DMV within five days if you:"
    ["Modify your vehicle's exhaust system"
     "Sell or transfer your vehicle"
     "Paint your vehicle a different color"]
    1]

   ["If you see orange construction signs/ cones on a freeway, you must:"
    ["Slow down because the lane ends ahead"
     "Be prepared for workers and equipment ahead"
     "Change lanes and maintain your current speed"]
    1]

   ["A law enforcement officer notices that on of your passengers is not wearing a seat belt and writes citation. Which of the following is true?"
    ["Both you and your passenger will receive a citation"
     "Your passenger will receive the citation, regardless of age"
     "You may receive the citation if the passenger is 15 or younger"]
    2]

   ["You are driving on the freeway behind a large truck. You should drive:"
    ["Closely behind in bad weather. The driver can see farther ahead"
     "Farther behind the truck than for a passenger car"
     "To the right side of the truck and wait to pass"]
    1]

   ["To see cars in your blind spots. You should check:"
    ["The inside rearview mirror"
     "The outside rearview mirror"
     "Over your shoulders"]
    2]

   ["While all of the following are dangerous to do while driving, which is also illegal?"
    ["Listening to music through a set of dual headphones"
     "Adjusting your outside mirrors"
     "Reading a road map"]
    0]

   ["Which of the following is true about roadways on bridges and overpasses in cold, wet weather?"
    ["They tend to freeze before the rest of the road does"
     "They do not freeze because they are made of concrete"
     "They tend to freeze after the rest of the road does"]
    0]

   ["You reach an intersection with stop signs on all for corners at the same time as the driver on your left. Who has the right-of- way?"
    ["The driver on your left has the right- of -way"
     "You have the right-of-way"
     "Whoever is signaling to make a turn has the right-of-way"]
    1]

   ["You must stop before you cross railroad tracks when:"
    ["You don't have room to completely cross the tracks"
     "The crossing is located in a city or town with frequent train traffic"
     "You transport two or more children in a passenger vehicle"]
    0]

   ["You are involved in a minor accident at an intersection. There are no injuries and little vehicle damage. You should:"
    ["Leave your vehicle in the traffic lane until law enforcement arrives"
     "Move your vehicle out of the traffic lane, if possible"
     "Not move your vehicle for any reason"]
    1]

   ["Tailgating other drivers (driving too close to their rear bumper):"
    ["Can frustrate other drivers and make them angry"
     "Cannot result in a traffic citation"
     "Reduces accident by preventing you from being 'cut off'"]
    0]
   
   ["Driving under the influence of any medication which impairs your driving is permitted:"
    ["Under no circumstances"
     "If you don't feel drowsy"
     "If it is prescribed by a physician"]
    0]

   ["You must notify DMV within 5 days of you:"
    ["Modify your vehicle's exhaust system"
     "Sell or transfer your vehicle"
     "Paint your vehicle a different color"]
    1]

   ["When you don't see any other vehicles around you while driving:"
    ["You may legally exceed the posted speed limit"
     "It is a good habit to signal for turns and lane changes anyway"
     "You do not need to stop completely for stop signs"]
    1]

   ["What is the benefit of a space cushion around your vehicle?"
    ["Other drivers can 'cut' in front of you, improving traffic flow"
     "If another driver makes a mistake, you have time to react"
     "It inflates to protect you from injury in case of an accident"]
    1]

   ["Backing your vehicle is:"
    ["Always dangerous to do"
     "Not dangerous if you have a helper"
     "Only dangerous in large vehicles"]
    0]

   ["When driving on a multilane street with other vehicles:"
    ["Drive alongside the other vehicles so the drivers can see you"
     "You should drive ahead of or behind the other vehicles"
     "It is safest to drive in the lane next to the center line"]
    1]

   ["Which of these statements is true about slippery road surfaces"
    ["Driving on wet leaves on the road will give you extra traction"
     "On cold days, shade from buildings/trees can hide spots of ice"
     "The pavement is less slippery when it first starts to rain on a hot day than it is afterwards"]
    1]

   ["It is most important to turn your front wheels toward the curb when you parked:"
    ["Facing downhill"
     "Facing uphill"
     "On a level road"]
    0]

   ["Unless otherwise posted, the speed limit in a business district is:"
    ["25 mph"
     "35 mph"
     "30 mph"]
    0]

   ["A bus has stopped ahead on your side of the road and is flashing its red lights.  What should you do?"
    ["Stop first, then proceed when you think it is safe"
     "Stop as long as the red lights flash"
     "Stop until all the children have crossed your lane"]
    1]

   ["You are driving on a four-lane freeway in the lane closest to the center divider.  To exit the freeway on the right you should:"
    ["Carefully cross all the lanes at one time"
     "Change lanes one at a time until you are in the proper lane"
     "Slow before beginning each lane change"]
    1]

   ["What is the best advice for driving when heavy fog or dust occurs?"
    ["Try not to drive until the conditions improve"
     "Do not drive too slowly, because other drivers may hit you"
     "Alternate your low and high beams to improve your vision"]
    0]

   ["If your vehicle is going to be hit from behind you should:"
    ["Be ready to brake after impact"
     "Hold down the brake pedal"
     "Shift to neutral and turn off the ignition"]
    0]

   ["Where should you stop your vehicle when there is no limit line?"
    ["Just pass the corner"
     "At the corner"
     "After the crosswalk"]
    1]

   ["When you are in a dedicated turn lane controlled by a green arrow, Which of the following is true?"
    ["All vehicles or pedestrians in the intersection must yield to you"
     "All oncoming vehicles and pedestrians are stopped by red lights"
     "You may turn in the arrow's direction without checking traffic"]
    1]

   ["You should usually drive your vehicle slower when:"
    ["You see brake lights coming on several vehicles ahead of you"
     "Passing large trucks on the freeway"
     "You want to look at a controlled accident scene"]
    0]

   ["There are two traffic lanes in your direction. You are driving in the left lane and many cars are passing you on the right. You should:"
    ["Stay in your lane so you don't impede the traffic flow"
     "Pull onto the left shoulder to let the other vehicles pass"
     "Move over into the right lane when it is safe"]
    2]

   ["You are driving slowly in the fast lane of a four-lane freeway.  There is traffic behind you in your lane. The driver behind you wishes to drive faster. You should:"
    ["Not change lanes if you are driving the speed limit"
     "Change lanes to the right when safe to do so"
     "Only change lanes if five or more vehicles are behind you"]
    1]

   ["You are required to wear your safety belt in a moving vehicle:"
    ["Unless your vehicle was manufactured before 1975"
     "Unless you are riding in the back of a pickup/camper"
     "If your vehicle is equipped with safety belts"]
    2]

   ["A large truck is driving in the middle of three lanes. You want to pass the large truck. it is best to pass:"
    ["Quickly on the left and move ahead of it"
     "Very slowly on the left and move ahead of it"
     "Very quickly on the right and move ahead of it"]
    0]

   ["If an oncoming vehicle has started to turn left in front of you:"
    ["Honk your horn to warn the driver and maintain your speed"
     "Maintain your speed and take your right-of-way"
     "Slow or stop your car to prevent an accident"]
    2]

   ["At dawn or dusk, or in rain or snow, it can be hard to see and be seen. A good way to let other drivers know you are there is to:"
    ["Use your horn once in a while"
     "Turn on your parking lights"
     "Turn on your low-beam headlights"]
    2]

   ["You were in an accident which caused more than $500 worth of damage. You must report the accident within 10 days to"
    ["The DMV"
     "The CHP"
     "Your insurance company"]
    0]

   ["Which of these vehicles must stop before crossing railroad tracks?"
    ["Tank trucks marked with hazardous materials placards"
     "Motor homes or pickup trucks towing a trailer"
     "Sport utility vehicles carrying four or more persons"]
    0]

   ["Driving faster than traffic and continually passing other cars:"
    ["Will get you to your destination much faster and safer"
     "Increases your chances of an accident"
     "Helps to prevent traffic congestion"]
    1]

   ["You want to turn left ahead. In the middle of the road there is a lane mark as shown.  You must:"
    ["Turn from your current traffic lane after signaling"
     "Merge completely into this lane before you make your left turn"
     "Not enter this lane for any reason"]
    1]

   ["You are driving on a road with only one lane in each direction.  You want to pass another vehicle, but there is a curve approaching which blocks your view of the road ahead. You must:"
    ["Not pass the other vehicle"
     "Increase your speed to pass safety"
     "Signal longer than five seconds to pass safely"]
    0]

   ["You may cross double, yellow lines to pass another vehicle if the:"
    ["Vehicle in front of you moves to the right to let you pass"
     "Yellow line next to your side of the road is broken"
     "Yellow line next to the other side of the road is broken"]
    1]

   ["When changing lanes on a freeway, you should:"
    ["Signal for at least five seconds"
     "Cross several lanes at a time to avoid slow down"
     "Avoid driving over broken white lines and lane markings"]
    0]

   ["Which of these statements is true about road construction zones?"
    ["Fines are the same for violations committed in construction zones"
     "You are responsible for the safety of the road workers"
     "Slow down only if you think workers are present"]
    1]

   ["You are driving on a one-way street. You may turn left onto another one-way street:"
    ["Only if a sign permits the turn"
     "If traffic on the street moves to the right"
     "If traffic on the street moves to the left"]
    2]

   ["You should use your horn when:"
    ["Another vehicle is in your way"
     "It may help prevent an accident"
     "Another driver makes a mistake"]
    1]

   ["Animals may be transported in the back of a pickup truck only if:"
    ["The sides of the truck bed are at least 18 inches high"
     "They are properly secured"
     "The tailgate of the truck is closed"]
    1]

   ["What is the difference between traffic lights with red arrows and those with solid red lights?"
    ["Red arrows are only used to stop traffic which is turning left"
     "Red arrows are only used for protected turn lanes"
     "You cannot turn against red arrow, even if you stop first"]
    2]

   ["When planning to pass another vehicle you should:"
    ["Not assume they will make space for you to return to your lane"
     "Assume they will let you pass if you use your turn signal"
     "Assume they will maintain a constant speed"]
    0]

   ["You should not start across an intersection if you know you will block the intersection when the light turns red:"
    ["Under any circumstances"
     "Unless you enter the intersection on a green arrow"
     "Unless you enter the intersection on a green light"]
    0]

   ["You are approaching a sharp curve in the road. You should:"
    ["Start braking before you enter the curve"
     "Start braking as soon as you enter the curve"
     "Accelerate into the curve and brake out of it"]
    0]

   ["Which of the following about safety belts?"
    ["They make it more likely you will drown if your car goes into a lake or river"
     "They keep you from being thrown clear to safety, which lowers your chances of surviving accidents"
     "They increase your chances of survival in most types of accidents"]
    2]

   ["When sharing the road with a trolley or light rail vehicle:"
    ["Never turn in front of an approaching trolley or light rail vehicle"
     "Always pass a light rail vehicle or trolley slowly on the right"
     "Remember they are loud and move slowly like freight trains"]
    0]

   ["If five or more vehicles are following you on a narrow two-lane road, you should:"
    ["Move to the right side of your lane and drive slowly"
     "Pull off the road when it is safe and let them pass"
     "Continue driving because you have the right-of-way"]
    1]

   ["You park your car at the curb on a level two-way street. Before getting out of your car, you should:"
    ["Look for cars or bicycles on the traffic side of your vehicle"
     "Turn your front wheel towards the curb"
     "Make sure you are parked at least two feet from the curb"]
    0]

   ["Which of these is a legal U-turn?"
    ["On a highway where there is a paved opening for a turn"
     "150 feet away from a hill or curve"
     "Over two sets of double, yellow lines in the roadway"]
    0]

   ["If you are unable to see the road ahead while driving because of heavy rain or fog, and your wipers do not help, you should:"
    ["Slow down and continue driving"
     "Turn on your high beams and continue driving"
     "Pull off the road completely until visibility improves"]
    2]

   ["If you approach a curve or the top of a hill and you do not have a clear view of the road ahead you should:"
    ["Pull over and wait for the conditions to improve"
     "Use your high beam lights to be more visible"
     "Slow down so you can stop if necessary"]
    2]

   ["You are crossing an intersection and an emergency vehicle is approaching with a siren and flashing lights. You should:"
    ["Stop immediately in the intersection until it passes"
     "Pull to the right in the intersection and stop"
     "Continue through the intersection, pull to the right, and stop"]
    2]

   ["When driving near road construction zones, you should:"
    ["Slow down to watch the construction as you pass"
     "Step on your brakes just before you pass the construction"
     "Pass the construction zone carefully and avoid 'rubbernecking'"]
    2]

   ["You may legally park your car in front of a driveway:"
    ["If the driveway is in front of your house"
     "As long as it is parked for longer than 15 minutes"
     "Under no circumstances"]
    2]

   ["You see a car approaching from the rear. When you check your mirror again to change lanes, you no longer see the car. You should:"
    ["Look over your shoulder to be sure the car isn't in your blind spot"
     "Turn your signal on and change lanes slowly"
     "Signal, honk your horn, and change lanes quickly"]
    0]

   ["When parked on any hill, always set your parking brake and:"
    ["Leave your vehicle in neutral"
     "Keep your front wheels parallel to the road"
     "Leave your vehicle in gear or the 'park' position"]
    2]

   ["You are driving 55 mph on a two-lane highway, one lane in each direction, and want to pass the car ahead of you. To pass safely, you need to:"
    ["Wait until solid double, yellow lines separate the lanes"
     "Increase your speed to 65 mph"
     "Have at least a 10 to 12 second gap in the oncoming traffic"]
    2]])

(ae-ds/defentity Question [^:key description, choices, answer, category])

(defn create-db
  "Create questions in the DB"
  [req]
  ;; create the questions
  (doseq [[description choices answer] knowledge-db]
    (ae-ds/save! (Question. description choices answer "knowledge")))
  (doseq [[description choices answer] passenger-db]
    (ae-ds/save! (Question. description choices answer "passenger")))
  (doseq [[description choices answer] safety-db]
    (ae-ds/save! (Question. description choices answer "safety")))
  ;; done!
  (with-page (format "Created %s questions successfully"
                     (apply + (map count [knowledge-db passenger-db safety-db])))))
    
(defn list-db
  "Admin interface to list all questions"
  [req]
  (with-page
    (html
     [:p (str "Found " (count (ae-ds/query :kind Question)) " questions")]
     [:h2 "Knowledge Questions:"]
     [:ul (for [i (ae-ds/query :kind Question
                               :filter (= :category "knowledge"))]
            [:li (:description i)])]
     [:h2 "Passenger Questions:"]
     [:ul (for [i (ae-ds/query :kind Question
                               :filter (= :category "passenger"))]
            [:li (:description i)])]
     [:h2 "Safety Questions:"]
     [:ul (for [i (ae-ds/query :kind Question
                               :filter (= :category "safety"))]
            [:li (:description i)])])))
     
(defn delete-db
  "Delete all entries in the DB"
  [req]
  (let [questions (ae-ds/query :kind Question)
        total (count questions)]
    (ae-ds/delete! questions)
    (with-page (str "Deleted " total " questions from db."))))

(defn next-rand-question
  "Return the next random question from the db"
  [category]
  (rand-nth (ae-ds/query :kind Question
                         :filter (= :category category))))
