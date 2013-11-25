using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    class TrainTouchingGenerator : ITouchingSource
    {
        public List<LocationPair> getTouchingLocations(Dictionary<string, int> items)
        {
            List<LocationPair> l = new List<LocationPair>();

            l.Add(getTL("Portadown, (NIR) Rail Station", "Scarva, (NIR) Rail Station", items));
            l.Add(getTL("Scarva, (NIR) Rail Station", "Poyntzpass, (NIR) Rail Station", items));
            l.Add(getTL("Poyntzpass, (NIR) Rail Station", "Newry, (NIR) Rail Station", items));
            l.Add(getTL("Belfast, (NIR) Gt Victoria St (GVS)", "Adelaide, (NIR) Rail Station", items));
            l.Add(getTL("Adelaide, (NIR) Rail Station", "Balmoral, (NIR) Rail Station", items));
            l.Add(getTL("Balmoral, (NIR) Rail Station", "Finaghy, (NIR) Rail Station", items));
            l.Add(getTL("Finaghy, (NIR) Rail Station", "Dunmurry, (NIR) Rail Station", items));
            l.Add(getTL("Dunmurry, (NIR) Rail Station", "Derriaghy, (NIR) Rail Station", items));
            l.Add(getTL("Derriaghy, (NIR) Rail Station", "Lambeg, (NIR) Rail Station", items));
            l.Add(getTL("Lambeg, (NIR) Rail Station", "Hilden, (NIR) Rail Station", items));
            l.Add(getTL("Hilden, (NIR) Rail Station", "Lisburn, (NIR) Rail Station", items));
            l.Add(getTL("Belfast, (NIR) Central Rail Station", "Botanic, (NIR) Rail Station", items));
            l.Add(getTL("Botanic, (NIR) Rail Station", "City Hospital, (NIR) Rail Station", items));
            l.Add(getTL("City Hospital, (NIR) Rail Station", "Lisburn, (NIR) Rail Station", items));
            l.Add(getTL("Lisburn, (NIR) Rail Station", "Moira, (NIR) Rail Station", items));
            l.Add(getTL("Moira, (NIR) Rail Station", "Lurgan, (NIR) Rail Station", items));
            l.Add(getTL("Lurgan, (NIR) Rail Station", "Portadown, (NIR) Rail Station", items));
            l.Add(getTL("Belfast, (NIR) Gt Victoria St (GVS)", "Lisburn, (NIR) Rail Station", items));
            l.Add(getTL("Bangor, (NIR) Rail Station", "Bangor West, (NIR) Rail Station", items));
            l.Add(getTL("Bangor West, (NIR) Rail Station", "Carnalea, (NIR) Rail Station", items));
            l.Add(getTL("Carnalea, (NIR) Rail Station", "Helens Bay, (NIR) Rail Station", items));
            l.Add(getTL("Helens Bay, (NIR) Rail Station", "Seahill, (NIR) Rail Station", items));
            l.Add(getTL("Seahill, (NIR) Rail Station", "Cultra, (NIR) Rail Station", items));
            l.Add(getTL("Cultra, (NIR) Rail Station", "Marino, (NIR) Rail Station", items));
            l.Add(getTL("Marino, (NIR) Rail Station", "Holywood, (NIR) Rail Station", items));
            l.Add(getTL("Holywood, (NIR) Rail Station", "Sydenham, (NIR) Station City Airport", items));
            l.Add(getTL("Sydenham, (NIR) Station City Airport", "Titanic Quarter, (NIR) Rail Station", items));
            l.Add(getTL("Titanic Quarter, (NIR) Rail Station", "Belfast, (NIR) Central Rail Station", items));
            l.Add(getTL("Belfast, (NIR) Central Rail Station", "Belfast, (NIR) Central Rail Station", items));
            l.Add(getTL("City Hospital, (NIR) Rail Station", "Belfast, (NIR) Gt Victoria St (GVS)", items));
            l.Add(getTL("Belfast, (NIR) Gt Victoria St (GVS)", "Belfast, (NIR) Gt Victoria St (GVS)", items));
            l.Add(getTL("Belfast, (NIR) Central Rail Station", "Portadown, (NIR) Rail Station", items));
            l.Add(getTL("Belfast, (NIR) Gt Victoria St (GVS)", "Dunmurry, (NIR) Rail Station", items));
            l.Add(getTL("Dunmurry, (NIR) Rail Station", "Lisburn, (NIR) Rail Station", items));
            l.Add(getTL("Bangor West, (NIR) Rail Station", "Holywood, (NIR) Rail Station", items));
            l.Add(getTL("Holywood, (NIR) Rail Station", "Titanic Quarter, (NIR) Rail Station", items));
            l.Add(getTL("Bangor, (NIR) Rail Station", "Belfast, (NIR) Central Rail Station", items));
            l.Add(getTL("Larne Harbour, (NIR) Rail Station", "Larne, (NIR) Town Rail Stn", items));
            l.Add(getTL("Larne, (NIR) Town Rail Stn", "Glynn, (NIR) Rail Station", items));
            l.Add(getTL("Glynn, (NIR) Rail Station", "Magheramorne, (NIR) Rail Station", items));
            l.Add(getTL("Magheramorne, (NIR) Rail Station", "Ballycarry, (NIR) Rail Station", items));
            l.Add(getTL("Ballycarry, (NIR) Rail Station", "Whitehead, (NIR) Rail Station", items));
            l.Add(getTL("Whitehead, (NIR) Rail Station", "Downshire, (NIR) Rail Station", items));
            l.Add(getTL("Downshire, (NIR) Rail Station", "Carrickfergus, (NIR) Rail Station", items));
            l.Add(getTL("Carrickfergus, (NIR) Rail Station", "Clipperstown, (NIR) Rail Station", items));
            l.Add(getTL("Clipperstown, (NIR) Rail Station", "Trooperslane, (NIR) Rail Station", items));
            l.Add(getTL("Trooperslane, (NIR) Rail Station", "Greenisland, (NIR) Rail Station", items));
            l.Add(getTL("Greenisland, (NIR) Rail Station", "Jordanstown, (NIR) Rail Station", items));
            l.Add(getTL("Jordanstown, (NIR) Rail Station", "Whiteabbey, (NIR) Rail Station", items));
            l.Add(getTL("Whiteabbey, (NIR) Rail Station", "Yorkgate, (NIR) Rail Station", items));
            l.Add(getTL("Yorkgate, (NIR) Rail Station", "Belfast, (NIR) Central Rail Station", items));
            l.Add(getTL("Carrickfergus, (NIR) Rail Station", "Belfast, (NIR) Central Rail Station", items));
            l.Add(getTL("Larne, (NIR) Town Rail Stn", "Whitehead, (NIR) Rail Station", items));
            l.Add(getTL("Jordanstown, (NIR) Rail Station", "Carrickfergus, (NIR) Rail Station", items));
            l.Add(getTL("Yorkgate, (NIR) Rail Station", "Mossley West, (NIR) Rail Station", items));
            l.Add(getTL("Mossley West, (NIR) Rail Station", "Antrim, (NIR) Rail Station", items));
            l.Add(getTL("Antrim, (NIR) Rail Station", "Antrim, (NIR) Rail Station", items));
            l.Add(getTL("Antrim, (NIR) Rail Station", "Ballymena, (NIR) Rail Station", items));
            l.Add(getTL("Ballymena, (NIR) Rail Station", "Cullybackey, (NIR) Rail Station", items));
            l.Add(getTL("Cullybackey, (NIR) Rail Station", "Ballymoney, (NIR) Rail Station", items));
            l.Add(getTL("Ballymoney, (NIR) Rail Station", "Coleraine, (NIR) Rail Station", items));
            l.Add(getTL("Coleraine, (NIR) Rail Station", "Coleraine, (NIR) Rail Station", items));
            l.Add(getTL("Coleraine, (NIR) Rail Station", "Castlerock, (NIR) Rail Station", items));
            l.Add(getTL("Castlerock, (NIR) Rail Station", "Bellarena, (NIR) Rail Station", items));
            l.Add(getTL("Bellarena, (NIR) Rail Station", "Derry, (NIR) Rail Station", items));
            l.Add(getTL("Coleraine, (NIR) Rail Station", "Coleraine Univ, (NIR) Rail Station", items));
            l.Add(getTL("Coleraine Univ, (NIR) Rail Station", "Dhu Varren, (NIR) Rail Station", items));
            l.Add(getTL("Dhu Varren, (NIR) Rail Station", "Portrush, (NIR) Rail Station", items));
            l.Add(getTL("Whiteabbey, (NIR) Rail Station", "Mossley West, (NIR) Rail Station", items));
            l.Add(getTL("Coleraine, (NIR) Rail Station", "Portrush, (NIR) Rail Station", items));
            l.Add(getTL("Newry, (NIR) Rail Station", "Dundalk, Rail Station", items));
            l.Add(getTL("Dundalk, Rail Station", "Drogheda, Rail Station", items));
            l.Add(getTL("Drogheda, Rail Station", "Dublin, (IE) Connolly Rail Station", items));
            l.Add(getTL("Portadown, (NIR) Rail Station", "Newry, (NIR) Rail Station", items));
            l.Add(getTL("Dundalk, Rail Station", "Dublin, (IE) Connolly Rail Station", items));
            l.Add(getTL("Belfast, (NIR) Central Rail Station", "Lisburn, (NIR) Rail Station", items));
            l.Add(getTL("Lisburn, (NIR) Rail Station", "Portadown, (NIR) Rail Station", items));
            l.Add(getTL("Lisburn, (NIR) Rail Station", "Lurgan, (NIR) Rail Station", items));


            return l;
        }

        private LocationPair getTL(string name, string name2, Dictionary<string, int> items)
        {
            int int1 = items[name];
            int int2 = items[name2];
            return new LocationPair(int1, int2);
        }
    }
}
