%val = rand(500000,1) * 449817.156250;

workload = load('test.workload.video');
val = workload;

val_int = round(val);
seconds_of_day = mod(val_int, 24*60*60);
index = floor(seconds_of_day / (10*60));
index = index + 1;

geo_loc_prob = rand(size(val,1),1);
geo_loc = zeros(size(val,1),1);

count = zeros(6,1);

for t = 1:size(val,1)
    loc = 1;
    sum_prob = trafico(index(t,1),loc);

    while loc < 6 && sum_prob < geo_loc_prob(t,1)
        loc = loc + 1;
        sum_prob = sum_prob + trafico(index(t,1),loc);
    end
    
    geo_loc(t,1) = loc;
    count(loc,1) = count(loc,1) + 1;
end

display(count);
save('geo_loc.workload.video','geo_loc','-ascii');