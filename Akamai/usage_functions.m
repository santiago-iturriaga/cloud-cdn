n = 24*6;

% North America
n_america = zeros(n,1);
t_0010=1;
t_0300=3*6;
n_america(t_0010:t_0300) = 16*10^6;
t_0830=8*6+3;
n_america(t_0300:t_0830) = linspace(16*10^6, 5.8*10^6, t_0830-t_0300+1);
t_1500=15*6;
n_america(t_0830:t_1500) = linspace(5.8*10^6, 16*10^6, t_1500-t_0830+1);
t_2400=24*6;
n_america(t_1500:t_2400) = 16*10^6;

% South America
s_america = zeros(n,1);
t_0030=3;
t_0130=1*6+3;
s_america(t_0030:t_0130) = 3.3*10^6;
t_0800=8*6;
s_america(t_0130:t_0800) = linspace(3.3*10^6, 0.21*10^6, t_0800-t_0130+1);
t_1430=14*6+3;
s_america(t_0800:t_1430) = linspace(0.21*10^6, 2.5*10^6, t_1430-t_0800+1);
t_2100=21*6;
s_america(t_1430:t_2100) = 2.5*10^6;
aux=linspace(2.5*10^6, 3.3*10^6, t_2400-t_2100+t_0030+1);
s_america(t_2100:t_2400) = aux(1:t_2400-t_2100+1);
s_america(t_0010:t_0030) = aux(t_2400-t_2100+1:t_2400-t_2100+t_0030-t_0010+1);

% Europe
europe = zeros(n,1);
t_1830=18*6+3;
t_2030=20*6+3;
europe(t_1830:t_2030) = 13.3*10^6;
t_0200=2*6;
aux=linspace(13.3*10^6, 2*10^6, t_2400-t_2030+t_0200+1);
europe(t_2030:t_2400) = aux(1:t_2400-t_2030+1);
europe(t_0010:t_0200) = aux(t_2400-t_2030+1:t_2400-t_2030+t_0200-t_0010+1);
europe(t_0200:t_0830) = linspace(2*10^6, 9.2*10^6, t_0830-t_0200+1);
t_1630=16*6+3;
europe(t_0830:t_1630) = linspace(9.2*10^6, 11.4*10^6, t_1630-t_0830+1);
europe(t_1630:t_1830) = linspace(11.4*10^6, 13.3*10^6, t_1830-t_1630+1);

% Asia
asia = zeros(n,1);
t_1930=19*6+3;
t_1230=12*6+3;
asia(t_1930:t_2100) = 2.5*10^6;
aux=linspace(2.5*10^6, 8.9*10^6, t_2400-t_2100+t_1230+1);
asia(t_2100:t_2400) = aux(1:t_2400-t_2100+1);
asia(t_0010:t_1230) = aux(t_2400-t_2100+1:t_2400-t_2100+t_1230-t_0010+1);
asia(t_1230:t_1500) = 8.9*10^6;
asia(t_1500:t_1930) = linspace(8.9*10^6, 2.5*10^6, t_1930-t_1500+1);

% Africa
africa = zeros(n,1);
t_0430=4*6+3;
africa(t_0010:t_0430) = linspace(0.5*10^6, 0.19*10^6, t_0430-t_0010+1);
africa(t_0430:t_1230) = linspace(0.19*10^6, 0.5*10^6, t_1230-t_0430+1);
africa(t_1230:t_2400) = 0.5*10^6;

% Australia
australia = zeros(n,1);
t_0530=5*6+3;
t_1030=10*6+3;
t_1730=17*6+3;
australia(t_1030:t_1730) = linspace(0.66*10^6, 0.1*10^6, t_1730-t_1030+1);
australia(t_1730:t_2400) = linspace(0.1*10^6, 0.52*10^6, t_2400-t_1730+1);
australia(t_0010:t_0530) = 0.52*10^6;
australia(t_0530:t_1030) = linspace(0.52*10^6, 0.66*10^6, t_1030-t_0530+1);

norm_africa = zeros(144,1);
norm_asia = zeros(144,1);
norm_australia = zeros(144,1);
norm_europe = zeros(144,1);
norm_n_america = zeros(144,1);
norm_s_america = zeros(144,1);

for i = 1:144
    aux = africa(i,1) + asia(i,1) + australia(i,1) + europe(i,1) + n_america(i,1) + s_america(i,1);
    norm_africa(i,1) = africa(i,1) / aux;
    norm_asia(i,1) = asia(i,1) / aux;
    norm_australia(i,1) = australia(i,1) / aux;
    norm_europe(i,1) = europe(i,1) / aux;
    norm_n_america(i,1) = n_america(i,1) / aux;
    norm_s_america(i,1) = s_america(i,1) / aux;
end

trafico = [norm_africa, norm_asia, norm_australia, norm_europe, norm_n_america, norm_s_america]

%x = [1:n];
%plot(x,n_america);
%plot(x,s_america);
%plot(x,europe);
%plot(x,asia);
%plot(x,africa);
%plot(x,australia);