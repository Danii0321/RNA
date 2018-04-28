import tester.Tester;

interface ILoString {
  int length();

  String makeString(int length);
  
  String firstCodon();
  
  ILoString cutFirstCodon();
  
  ILoString cutFirst(int length);
  
  boolean stop();
  
  boolean start();
  
  ILoString toStart();
  
  ILoString makeProtein();
  
  ILoString cutRNA();
  
  ILoLoString translate();
}

class MtLoString implements ILoString {
  /* TEMPLATE
   * fields:
   * 
   * methods:
   * this.length()... int
   * this.makeString(int)... String
   * this.firstCodon()... String
   * this.cutFirst(int)... ILoString   
   * this.cutFirstCodon()... ILoString
   * this.stop()... boolean
   * this.start()... boolean
   * this.toStart()... ILoString
   * this.makeProtein(ILoString)... ILoString
   * this.cutRNA()... ILoString
   * this.translate()... ILoLoString
   * 
   * methods for fields:
   */
  
  //returns a length of zero.
  public int length() {
    return 0;
  }

  //returns an empty string
  public String makeString(int length) {
    return "";
  }
  
  //returns an empty string
  public String firstCodon() {
    return "";
  }
  
  //returns an MtLoString
  public ILoString cutFirstCodon() {
    return this;
  }
  
  public ILoString cutFirst(int length) {
    return this;
  }
  
  //says that an empty codon is not a stop codon
  public boolean stop() {
    return false;
  }
  
  //says that an empty string is not a start codon
  public boolean start() {
    return false;
  }
  
  //returns an MtLoString
  public ILoString toStart() {
    return this;
  }
  
  //returns an MtLoString
  public ILoString makeProtein() {
    return this;
  }
  
  public ILoString cutRNA() {
    return this;
  }
  
  //returns an empty list of proteins
  public ILoLoString translate() {
    return new MtLoLoString();
  }
}

class ConsLoString implements ILoString {
  //fields
  String first;
  ILoString rest;

  //constructor
  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  /* TEMPLATE
   * fields:
   * this.first... String
   * this.rest... ILoString
   * 
   * methods:
   * this.length()... int
   * this.makeString(int)... String
   * this.firstCodon()... String
   * this.cutFirst(int)... ILoString   
   * this.cutFirstCodon()... ILoString
   * this.stop()... boolean
   * this.start()... boolean
   * this.toStart()... ILoString
   * this.makeProtein(ILoString)... ILoString
   * this.cutRNA()... ILoString
   * this.translate()... ILoLoString
   * 
   * methods for fields:
   * this.rest.length()... int
   * this.rest.makeString(int)... String
   * this.rest.firstCodon()... String
   * this.rest.cutFirst(int)... ILoString
   * this.rest.cutFirstCodon()... ILoString
   * this.stop()... boolean
   * this.start()... boolean
   * this.toStart()... ILoString
   * this.makeProtein(ILoString)... ILoString
   * this.cutRNA()... ILoString
   * this.translate()... ILoLoString
   */

  //tells the list's length
  public int length() {
    return 1 + this.rest.length();
  }

  //turns the last (outermost) length base pairs into a single string
  public String makeString(int length) {
    if (length <= 1) {
      return this.first;
    }
    else {
      return this.first + this.rest.makeString(length - 1);
    }
  }

  
  //returns the first (innermost) three bases in a ILoString
  //only meant for ConsLoStrings 3 BPs or longer in length
  //if it's too short, returns ""
  public String firstCodon() {
    return this.makeString(3);
  }
  
  //returns a ILoString with the first (innermost) length BPs replaced with MtLoString
  //only meant for ConsLoStrings length BPs or longer in length
  //if it's too short, returns MtLoString
  public ILoString cutFirst(int length) {
    if (length <= 1) {
      return this.rest;
    }
    else {
      return this.rest.cutFirst(length - 1);
    }
  }
  
  //returns a ILoString with the first (innermost) 3 BP replaced with MtLoString
  //only meant for ConsLoStrings 3 BPs or longer in length
  //if it's too short, returns MtLoString
  public ILoString cutFirstCodon() {
    return this.cutFirst(3);
  }

  //tells whether the first (innermost) codon is a stop codon
  public boolean stop() {
    return this.firstCodon().equals("UAG") 
        || this.firstCodon().equals("UAA") 
        || this.firstCodon().equals("UGA");
  }
  
  //tells whether the first (innermost) codon is a start codon
  public boolean start() {
    return this.firstCodon().equals("AUG");
  }
  
  //removes all base pairs until the next start codon
  //stops if the RNA gets too short
  public ILoString toStart() {
    if (this.start() || this.length() < 3) {
      return this;
    }
    else {
      return this.cutFirstCodon().toStart();
    }
  }
  
  //returns the longest protein that abides by the rules
  //if the stop codon is first, returns an empty list
  //if the list is too short, return an empty list
  public ILoString makeProtein() {
    if (this.length() < 3 || this.stop()) {
      return new MtLoString();
    }
    else {
      return new ConsLoString(this.firstCodon(), this.cutFirstCodon().makeProtein());
    }
  }
  
  //removes all of the base-pairs up until the next stop codon
  //stops if the RNA gets too short
  public ILoString cutRNA() {
    if (this.length() < 3) {
      return this;
    }
    else if (this.stop()) {
      return this.cutFirstCodon();
    }
    else {
      return this.cutFirstCodon().cutRNA();
    }
  }
  
  //makes a list of lists of strings. each list of strings represents a protein.
  //starts only when it sees a start codon,
  //ends when it reaches an end codon or the RNA gets too short
  public ILoLoString translate() {
    ILoString chopped = this.toStart();
    if (chopped.length() >= 3) {
      ILoString protein = chopped.makeProtein();
      return new ConsLoLoString(protein, chopped.cutRNA().translate());
    }
    else {
      return new MtLoLoString();
    }
  }
  
  
}

interface ILoLoString {}

class MtLoLoString implements ILoLoString {}

class ConsLoLoString implements ILoLoString {
  //fields
  ILoString first;
  ILoLoString rest;

  //constructor
  ConsLoLoString(ILoString first, ILoLoString rest) {
    this.first = first;
    this.rest = rest;
  }
}

class ExamplesRNA {
  //examples of RNA
  ILoString emptyRNA = new MtLoString();
  ILoString s1 = new ConsLoString("A", emptyRNA);
  ILoString s2 = new ConsLoString("U", s1);
  ILoString s3 = new ConsLoString("C", s2);
  ILoString s3b = new ConsLoString("A", s3);
  ILoString s3c = new ConsLoString("G", s3b);
  ILoString s4 = new ConsLoString("G", new ConsLoString("U", new ConsLoString("A", s3)));
  ILoString s5 = new ConsLoString("A", new ConsLoString("A", new ConsLoString("A", s4)));
  ILoString s6 = new ConsLoString("A", new ConsLoString("A", new ConsLoString("U", s5)));
  ILoString s7 = new ConsLoString("A", new ConsLoString("A", new ConsLoString("U", s6)));
  ILoString s8 = new ConsLoString("G", new ConsLoString("U", new ConsLoString("A", s7)));
  ILoString s9 = new ConsLoString("C", new ConsLoString("A", new ConsLoString("G", s8)));
  ILoString s10 = new ConsLoString("A", new ConsLoString("A", new ConsLoString("U", s9)));
  ILoString s11 = new ConsLoString("A", new ConsLoString("A", new ConsLoString("U", s10)));
  
  //examples of proteins
  ILoLoString emptyProtein = new MtLoLoString();
  ILoLoString p1 = new ConsLoLoString(new ConsLoString("AUG", emptyRNA), emptyProtein);
  ILoLoString p2 = new ConsLoLoString(new ConsLoString("AAA", new 
      ConsLoString("AUG", emptyRNA)), emptyProtein);
  ILoLoString p3 = new ConsLoLoString(new ConsLoString("GAC", new 
      ConsLoString("AUG", emptyRNA)), p2);
  
  //tests of methods:
  //test for length()
  boolean testLength(Tester t) {
    return t.checkExpect(emptyRNA.length(), 0)
        && t.checkExpect(s1.length(), 1)
        && t.checkExpect(s4.length(), 6);
  }
  
  //test for makeString()
  boolean testMakeString(Tester t) {
    return t.checkExpect(emptyRNA.makeString(0), "")
        && t.checkExpect(emptyRNA.makeString(2), "")
        && t.checkExpect(s1.makeString(1), "A")
        && t.checkExpect(s1.makeString(3), "A")
        && t.checkExpect(s3b.makeString(3), "UCA");
  }
  
  //test for firstCodon()
  boolean testFirstCodon(Tester t) {
    return t.checkExpect(emptyRNA.firstCodon(), "")
        && t.checkExpect(s2.firstCodon(), "")
        && t.checkExpect(s3.firstCodon(), "AUC")
        && t.checkExpect(s3c.firstCodon(), "AUC")
        && t.checkExpect(s4.firstCodon(), "AUC");
  }
  
  //test for cutFirst(int)
  boolean testCutFirst(Tester t) {
    return t.checkExpect(emptyRNA.cutFirst(2), emptyRNA)
        && t.checkExpect(s2.cutFirst(1), new ConsLoString("U", emptyRNA))
        && t.checkExpect(s2.cutFirst(2), emptyRNA)
        && t.checkExpect(s3.cutFirst(2), new ConsLoString("C", emptyRNA));
  }
  
  //test for cutFirstCodon()
  boolean testCutFirstCodon(Tester t) {
    return t.checkExpect(emptyRNA.cutFirstCodon(), emptyRNA)
        && t.checkExpect(s2.cutFirstCodon(), emptyRNA)
        && t.checkExpect(s3b.cutFirstCodon(), s1);
  }
  
  //test for stop()
  boolean testStop(Tester t) {
    return t.checkExpect(emptyRNA.stop(), false)
        && t.checkExpect(s1.stop(), false)
        && t.checkExpect(s3.stop(), false)
        && t.checkExpect(s4.stop(), false)
        && t.checkExpect(new ConsLoString("G", new ConsLoString("A", new 
            ConsLoString("U", emptyRNA))).stop(), true)
        && t.checkExpect(new ConsLoString("A", new ConsLoString("A", new 
            ConsLoString("U", emptyRNA))).stop(), true)
        && t.checkExpect(new ConsLoString("A", new ConsLoString("G", new 
            ConsLoString("U", emptyRNA))).stop(), true)
        && t.checkExpect(new ConsLoString("C", new ConsLoString("G", new 
            ConsLoString("A", new ConsLoString("U", emptyRNA)))).stop(), true);
  }
  
  //test for start()
  boolean testStart(Tester t) {
    return t.checkExpect(emptyRNA.start(), false)
        && t.checkExpect(s1.start(), false)
        && t.checkExpect(s3.start(), false)
        && t.checkExpect(s4.start(), false)
        && t.checkExpect(new ConsLoString("G", new ConsLoString("U", new 
            ConsLoString("A", emptyRNA))).start(), true)
        && t.checkExpect(new ConsLoString("A", new ConsLoString("G", new 
            ConsLoString("U", new ConsLoString("A", emptyRNA)))).start(), true);
  }
  
  //test for toStart()
  boolean testToStart(Tester t) {
    return t.checkExpect(emptyRNA.toStart(), emptyRNA)
        && t.checkExpect(s1.toStart(), s1)
        && t.checkExpect(s3b.toStart(), s1)
        && t.checkExpect(s4.toStart(), new ConsLoString("G", new ConsLoString("U", new 
            ConsLoString("A", emptyRNA))));
  }
  
  //test for makeProtein(ILoString)
  /*
  boolean testMakeProtein(Tester t) {
    return t.checkExpect(emptyRNA.makeProtein(emptyRNA), emptyRNA)
        && t.checkExpect(s1.makeProtein(emptyRNA), emptyRNA)
        && t.checkExpect(s2.makeProtein(emptyRNA), emptyRNA)
        && t.checkExpect(s3.makeProtein(emptyRNA), new ConsLoString("AUC", emptyRNA))
        && t.checkExpect(s3c.makeProtein(emptyRNA), new ConsLoString("AUC", emptyRNA))
        && t.checkExpect(s6.makeProtein(emptyRNA), new ConsLoString("AAA", new 
            ConsLoString("AUG", new ConsLoString("AUC", emptyRNA))))
        && t.checkExpect(s7.makeProtein(emptyRNA), new ConsLoString("AAA", new 
            ConsLoString("AUG", new ConsLoString("AUC", emptyRNA))))
        && t.checkExpect(s8.makeProtein(emptyRNA), new ConsLoString("AAA", new 
            ConsLoString("AUG", new ConsLoString("AUC", emptyRNA))));
  }*/
  
  //test for cutRNA
  boolean testCutRNA(Tester t) {
    return t.checkExpect(emptyRNA.cutRNA(), emptyRNA)
        && t.checkExpect(s1.cutRNA(), s1)
        && t.checkExpect(s3b.cutRNA(), s1)
        && t.checkExpect(s4.cutRNA(), emptyRNA)
        && t.checkExpect(s6.cutRNA(), emptyRNA)
        && t.checkExpect(s7.cutRNA(), new ConsLoString("A", new 
            ConsLoString("A", new ConsLoString("U", emptyRNA))));
  }
  
  //test for translate()
  boolean testTranslate(Tester t) {
    return t.checkExpect(emptyRNA.translate(), emptyProtein)
        && t.checkExpect(s1.translate(), emptyProtein)
        && t.checkExpect(s3.translate(), emptyProtein)
        && t.checkExpect(s4.translate(), p1)
        && t.checkExpect(s5.translate(), p2)
        && t.checkExpect(s6.translate(), p2)
        && t.checkExpect(s9.translate(), p3)
        && t.checkExpect(s10.translate(), p3)
        && t.checkExpect(s11.translate(), p3);
  }
}