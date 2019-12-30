using System;
using System.Collections.Generic;
using System.Text;

namespace Algorithms
{
    class StringMatch
    {
        public int Find(string s, string t)
        {
            int m = t.Length;
            int[] next = new int[m];
            next[0] = -1;

            for (int i = 1; i < m; i++)
            {
                int j = next[i - 1];
                while (j >= 0 && t[i - 1] != t[j])
                    j = next[j];
                next[i] = j + 1;
            }

           /* for (int i = 0; i < m; i++)
            {
                Console.Write(next[i] + " ");
            }
            Console.WriteLine();*/

            int sp = 0, tp = 0;
            while (sp < s.Length)
            {
                if (t[tp] == s[sp])
                {
                    tp++;
                    sp++;
                }
                else
                {
                    tp = next[tp];
                    if (tp == -1)
                    {
                        tp = 0;
                        sp++;
                    }
                }
                if (tp == m)
                    return sp - m;
            }

            return -1;
        }
    }
}
